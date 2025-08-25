package io.github.hayato2158.lifescore.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ScoreRecord::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao
}