package io.github.hayato2158.lifescore.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth // YearMonth をインポート
import java.time.format.DateTimeFormatter // DateTimeFormatter をインポート
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScoreRepository @Inject constructor(
    private val scoreDao: ScoreDao,
    private val clock: Clock
) {


    fun all(): Flow<List<ScoreRecord>> = scoreDao.observeAll()

    suspend fun saveToday(score: Int) {
        val today = LocalDate.now(clock).format(DateTimeFormatter.ISO_LOCAL_DATE) // YYYY-MM-DD
        scoreDao.upsert(ScoreRecord(date = today, score = score))
    }

    suspend fun updateMemo(record: ScoreRecord, memo: String?)  {
        scoreDao.upsert(record.copy(memo = memo))
    }

    // 新しく追加するメソッド
    suspend fun getMonthlySummary(yearMonth: YearMonth): MonthlySummary {
        return withContext(Dispatchers.IO) { // IOスレッドでDBアクセス
            val yearMonthStr = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
            val scoreAndCount = scoreDao.getMonthlyScoreAndCount(yearMonthStr)

            val totalScore = scoreAndCount.totalScore ?: 0 // nullの場合は0とする
            val recordCount = scoreAndCount.recordCount

            val averageScore = if (recordCount > 0) {
                totalScore.toDouble() / recordCount
            } else {
                0.0
            }
            MonthlySummary(
                totalScore = totalScore,
                averageScore = averageScore,
                recordCount = recordCount
            )
        }
    }
}
