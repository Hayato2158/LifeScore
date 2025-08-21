package io.github.hayato2158.lifescore

import android.app.Application
import androidx.room.Room
import dagger.hilt.android.HiltAndroidApp
import io.github.hayato2158.lifescore.data.AppDatabase
import io.github.hayato2158.lifescore.data.ScoreRepository

@HiltAndroidApp
class App: Application(){


    lateinit var repository: ScoreRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app.db"
        ).build()


        repository = ScoreRepository(db.scoreDao())
    }
}