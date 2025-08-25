package io.github.hayato2158.lifescore.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ScoreRecord::class],
    version = 2,
    exportSchema = true) // version を 1 から 2 に変更
abstract class AppDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    // マイグレーション処理は AppModule で Room.databaseBuilder に追加するのが一般的
     companion object {
         val MIGRATION_1_2 = object : Migration(1, 2) {
             override fun migrate(db: SupportSQLiteDatabase) {
                 db.execSQL("ALTER TABLE score_records ADD COLUMN memo TEXT")
             }
         }
     }
}