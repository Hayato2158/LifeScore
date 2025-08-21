package io.github.hayato2158.lifescore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// viewModelFactory と initializer はHiltでは不要になる
// import androidx.lifecycle.viewmodel.initializer
// import androidx.lifecycle.viewmodel.viewModelFactory
// import io.github.hayato2158.lifescore.App // Appクラスの直接参照も不要になる
import dagger.hilt.android.lifecycle.HiltViewModel // HiltViewModelをインポート
import io.github.hayato2158.lifescore.data.ScoreRecord // emptyListの型推論のため、または明示的に指定
import io.github.hayato2158.lifescore.data.ScoreRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject // Injectをインポート

@HiltViewModel // HiltがこのViewModelを生成できるようにする
class ScoresViewModel @Inject constructor( // コンストラクタに@Injectアノテーションを追加
    private val repo: ScoreRepository
) : ViewModel(){

    val items = repo.all()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList<ScoreRecord>()) // 型を明示するか、ScoreRecordのimportが必要

    fun saveToday(score: Int) {
        viewModelScope.launch {
            repo.saveToday(score)
        }
    }
}
