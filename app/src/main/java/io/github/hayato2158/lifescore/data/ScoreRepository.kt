package io.github.hayato2158.lifescore.data

import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.LocalDate

class ScoreRepository(
    private val dao: ScoreDao,
    private val clock: Clock = Clock.systemDefaultZone()
) {
    fun today(): Flow<ScoreRecord?> =
        dao.observeByDate(LocalDate.now(clock).toString())

    fun all(): Flow<List<ScoreRecord>> =
        dao.observeAll()

    suspend fun saveToday(score: Int) {
        dao.upsert(ScoreRecord(LocalDate.now(clock).toString(), score))
    }
}