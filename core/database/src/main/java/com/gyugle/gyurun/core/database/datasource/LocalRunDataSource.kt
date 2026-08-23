package com.gyugle.gyurun.core.database.datasource

import androidx.paging.PagingData
import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.common.Result
import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.RunId
import com.gyugle.gyurun.core.domain.weather.Weather
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface LocalRunDataSource {
    fun getRuns(): Flow<List<Run>>

    fun getRunsSince(sinceUtc: ZonedDateTime): Flow<List<Run>>

    fun getMostRecentRun(): Flow<Run?>

    fun getRunsPaged(): Flow<PagingData<Run>>

    fun getRun(id: RunId): Flow<Run?>

    suspend fun upsertRun(
        run: Run,
        mapPicturePath: String?,
    ): Result<RunId, DataError.Local>

    suspend fun updateWeather(
        id: RunId,
        weather: Weather,
    ): EmptyResult<DataError.Local>

    suspend fun deleteRun(id: RunId)
}