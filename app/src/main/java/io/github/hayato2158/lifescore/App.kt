package io.github.hayato2158.lifescore

import android.app.Application
import androidx.room.Room
import dagger.hilt.android.HiltAndroidApp
import io.github.hayato2158.lifescore.data.AppDatabase
import io.github.hayato2158.lifescore.data.ScoreRepository

@HiltAndroidApp
class App: Application()