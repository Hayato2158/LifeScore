package io.github.hayato2158.lifescore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score_records")
data class ScoreRecord(
    @PrimaryKey val date: String,
    val score: Int
)