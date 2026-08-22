package com.gyugle.gyurun.core.domain.run.repository

import androidx.paging.PagingData
import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.RunId
import com.gyugle.gyurun.core.domain.weather.Weather
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface RunRepository {
    fun getRuns(): Flow<List<Run>>

    /**
     * 최근 며칠만 쓰는 곳을 위함.
     */
    fun getRunsSince(sinceUtc: ZonedDateTime): Flow<List<Run>>

    fun getMostRecentRun(): Flow<Run?>

    fun getRunsPaged(): Flow<PagingData<Run>>

    fun getRun(id: RunId): Flow<Run?>

    suspend fun upsertRun(
        run: Run,
        mapPicture: ByteArray,
    ): EmptyResult<DataError.Local>

    suspend fun updateWeather(
        id: RunId,
        weather: Weather,
    ): EmptyResult<DataError.Local>

    suspend fun deleteRun(run: Run)

    suspend fun restoreRun(run: Run): EmptyResult<DataError.Local>

    suspend fun finalizeRunDeletion(run: Run)

}