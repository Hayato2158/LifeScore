package io.github.hayato2158.lifescore.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.hayato2158.lifescore.data.AppDatabase
import io.github.hayato2158.lifescore.data.ScoreDao
import io.github.hayato2158.lifescore.data.ScoreRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Application全体でシングルトンとして提供
object AppModule {

    @Provides
    @Singleton // AppDatabaseはシングルトンにする
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app.db" // データベース名
        ).build()
    }

    @Provides
    fun provideScoreDao(appDatabase: AppDatabase): ScoreDao {
        return appDatabase.scoreDao()
    }

    @Provides
    @Singleton // ScoreRepositoryもシングルトンにするのが一般的
    fun provideScoreRepository(scoreDao: ScoreDao): ScoreRepository {
        return ScoreRepository(scoreDao)
    }
}
