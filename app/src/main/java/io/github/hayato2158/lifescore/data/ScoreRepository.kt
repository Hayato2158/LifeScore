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

    suspend fun save(score: Int,date: LocalDate) {
        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val existing = scoreDao.findByDate(dateStr)
        val keptMemo = existing?.memo
        scoreDao.upsert(ScoreRecord(date = dateStr, score = score, memo = keptMemo))
    }

    suspend fun saveToday(score: Int) {
        save(score, LocalDate.now(clock))
    }

    suspend fun updateMemo(record: ScoreRecord, memo: String?)  {
        scoreDao.upsert(record.copy(memo = memo))
    }

    suspend fun delete(record: ScoreRecord) {
        scoreDao.delete(record)
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
