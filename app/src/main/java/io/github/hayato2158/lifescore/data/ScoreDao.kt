package io.github.hayato2158.lifescore.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Query("SELECT * FROM score_records WHERE date = :date LIMIT 1")
    fun observeByDate(date: String): Flow<ScoreRecord?>

    @Query("SELECT * FROM score_records ORDER BY date DESC")
    fun observeAll(): Flow<List<ScoreRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ScoreRecord)

    // 新しく追加するクエリ
    // 指定された年月の合計スコアと記録数を取得する
    // date は "YYYY-MM-DD" 形式なので、"YYYY-MM" で前方一致検索する
    // テーブル名は既存のクエリに合わせて "score_records" を使用
    @Query("SELECT SUM(score) as totalScore, COUNT(date) as recordCount FROM score_records WHERE date LIKE :yearMonth || '%'")
    suspend fun getMonthlyScoreAndCount(yearMonth: String): MonthlyScoreAndCount

    // 合計と件数だけを保持するシンプルなデータクラス (ScoreDao内で使用)
    data class MonthlyScoreAndCount(
        val totalScore: Int?, // レコードがない場合nullになる可能性がある
        val recordCount: Int
    )
}
