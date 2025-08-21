package io.github.hayato2158.lifescore.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao{
    @Query("SELECT * FROM score_records WHERE date = :date LIMIT 1")
    fun observeByDate(date: String): Flow<ScoreRecord?>

    @Query("SELECT * FROM score_records ORDER BY date DESC")
    fun observeAll(): Flow<List<ScoreRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ScoreRecord)
}