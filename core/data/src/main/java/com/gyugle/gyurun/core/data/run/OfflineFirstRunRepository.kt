package com.gyugle.gyurun.core.data.run

import androidx.paging.PagingData
import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.common.Result
import com.gyugle.gyurun.core.common.asEmptyResult
import com.gyugle.gyurun.core.database.datasource.LocalRunDataSource
import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.RunId
import com.gyugle.gyurun.core.domain.run.repository.RunRepository
import com.gyugle.gyurun.core.domain.run.repository.WeatherBackfillScheduler
import com.gyugle.gyurun.core.domain.weather.Weather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

internal class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val runMapStorage: RunMapStorage,
    private val applicationScope: CoroutineScope,
    private val weatherBackfillScheduler: WeatherBackfillScheduler,
) : RunRepository {
    override fun getRuns(): Flow<List<Run>> = localRunDataSource.getRuns()

    override fun getRunsSince(sinceUtc: ZonedDateTime): Flow<List<Run>> =
        localRunDataSource.getRunsSince(sinceUtc)

    override fun getMostRecentRun(): Flow<Run?> = localRunDataSource.getMostRecentRun()

    override fun getRunsPaged(): Flow<PagingData<Run>> = localRunDataSource.getRunsPaged()

    override fun getRun(id: RunId): Flow<Run?> = localRunDataSource.getRun(id)

    override suspend fun upsertRun(
        run: Run,
        mapPicture: ByteArray,
    ): EmptyResult<DataError.Local> =
        applicationScope
            .async {
                val mapPicturePath = runMapStorage.savePicture(mapPicture)
                val localResult = localRunDataSource.upsertRun(run, mapPicturePath)
                if (localResult is Result.Success && run.weather == null) {
                    weatherBackfillScheduler.scheduleBackfill(localResult.data)
                }
                localResult.asEmptyResult()
            }.await()

    override suspend fun updateWeather(
        id: RunId,
        weather: Weather,
    ): EmptyResult<DataError.Local> = localRunDataSource.updateWeather(id, weather)

    override suspend fun deleteRun(run: Run) {
        applicationScope
            .async {
                val id = run.id ?: return@async
                localRunDataSource.deleteRun(id)
            }.await()
    }

    override suspend fun restoreRun(run: Run): EmptyResult<DataError.Local> =
        applicationScope
            .async {
                localRunDataSource.upsertRun(run, run.mapPicturePath).asEmptyResult()
            }.await()

    override suspend fun finalizeRunDeletion(run: Run) {
        applicationScope
            .async {
                runMapStorage.deletePicture(run.mapPicturePath)
            }.await()
    }
}