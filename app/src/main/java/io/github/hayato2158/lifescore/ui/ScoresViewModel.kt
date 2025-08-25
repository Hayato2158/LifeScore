package io.github.hayato2158.lifescore.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.hayato2158.lifescore.data.MonthlySummary
import io.github.hayato2158.lifescore.data.ScoreRecord
import io.github.hayato2158.lifescore.data.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine // combine をインポート
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth // YearMonth をインポート
import java.time.format.DateTimeFormatter // DateTimeFormatter をインポート
import javax.inject.Inject

@HiltViewModel
class ScoresViewModel @Inject constructor(
    private val repo: ScoreRepository
) : ViewModel() {

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()

    private val _monthlySummary = MutableStateFlow<MonthlySummary?>(null)
    val monthlySummary: StateFlow<MonthlySummary?> = _monthlySummary.asStateFlow()

    private val _currentMemo = MutableStateFlow("")
    val currentMemo: StateFlow<String> = _currentMemo.asStateFlow()

    init {
        Log.d("ScoresViewModel", "ViewModel initialized. Repository instance: $repo")
        // 初期表示月でサマリを読み込む
        loadMonthlySummary(_currentYearMonth.value)
    }

    val allScores: StateFlow<List<ScoreRecord>> = repo.all() // all()メソッドの戻り値はFlow<List<ScoreRecord>>
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()) // 型推論でOK


    // 表示用のフォーマットされた年月文字列 (UIで使う用)
    // 初期値を確実に設定するために、少し冗長ですが以下のようにします
    private val monthFormatter = DateTimeFormatter.ofPattern("yyyy年MM月")
    val formattedYearMonth: StateFlow<String> = _currentYearMonth
        .combine(MutableStateFlow(monthFormatter)) { yearMonth, formatter -> // formatterをFlowにする必要はない
            yearMonth.format(formatter)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = YearMonth.now().format(monthFormatter) // 初期値を設定
        )

    val currentMonthScores: StateFlow<List<ScoreRecord>> =
        allScores.combine(currentYearMonth) { scores, yearMonth ->
            val yearMonthStr = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
            scores.filter { record ->
                record.date.startsWith(yearMonthStr) // "YYYY-MM-DD" が "YYYY-MM" で始まるかでフィルタ
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateMemo(newMemo: String) {
        _currentMemo.value = newMemo
    }

    fun updateRecordMemo(record: ScoreRecord, memo: String) {
        viewModelScope.launch {
            repo.updateMemo(record, memo.ifBlank { null })
        }
    }

    fun saveToday(score: Int) {
        viewModelScope.launch {
            repo.saveToday(score)
            loadMonthlySummary(_currentYearMonth.value)
        }
    }

    fun changeMonth(amount: Long) {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(amount)
        loadMonthlySummary(_currentYearMonth.value)
    }

    private fun loadMonthlySummary(yearMonth: YearMonth) {
        viewModelScope.launch {
            try {
                _monthlySummary.value = repo.getMonthlySummary(yearMonth)
            } catch (e: Exception) {
                Log.e("ScoresViewModel", "Failed to load monthly summary for $yearMonth", e)
                _monthlySummary.value = null
            }
        }
    }
}
