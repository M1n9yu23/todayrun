package com.gyugle.gyurun.core.database.datasource.impl

import android.database.sqlite.SQLiteFullException
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.common.Result
import com.gyugle.gyurun.core.database.dao.RunDao
import com.gyugle.gyurun.core.database.datasource.LocalRunDataSource
import com.gyugle.gyurun.core.database.mapper.toRun
import com.gyugle.gyurun.core.database.mapper.toRunEntity
import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.RunId
import com.gyugle.gyurun.core.domain.weather.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime

private const val RUNS_PAGE_SIZE = 20

internal class RoomLocalRunDataSource(
    private val runDao: RunDao
) : LocalRunDataSource {
    override fun getRuns(): Flow<List<Run>> =
        runDao.getRuns().map { runEntities ->
            runEntities.map { it.toRun() }
        }

    override fun getRunsSince(sinceUtc: ZonedDateTime): Flow<List<Run>> =
        runDao.getRunsSince(sinceUtc.toInstant().toString()).map { runEntities ->
            runEntities.map { it.toRun() }
        }

    override fun getMostRecentRun(): Flow<Run?> =
        runDao.getMostRecentRun().map { entity ->
            entity?.toRun()
        }

    override fun getRunsPaged(): Flow<PagingData<Run>> =
        Pager(
            config = PagingConfig(pageSize = RUNS_PAGE_SIZE),
        ) {
            runDao.getRunsPaged()
        }.flow.map { pagingData ->
            pagingData.map { it.toRun() }
        }

    override fun getRun(id: RunId): Flow<Run?> =
        runDao.getRun(id.value).map { entity ->
            entity?.toRun()
        }

    override suspend fun upsertRun(
        run: Run,
        mapPicturePath: String?
    ): Result<RunId, DataError.Local> =
        try {
            val entity = run.toRunEntity(mapPicturePath)
            runDao.upsertRun(entity)
            Result.Success(RunId(entity.id))
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }

    override suspend fun updateWeather(
        id: RunId,
        weather: Weather
    ): EmptyResult<DataError.Local> =
        try {
            runDao.updateWeather(
                id = id.value,
                weatherType = weather.type.name,
                temperatureCelsius = weather.temperatureCelsius,
                feelsLikeCelsius = weather.feelsLikeCelsius,
                humidityPercent = weather.humidityPercent,
                windSpeedKmh = weather.windSpeedKmh,
            )
            Result.Success(Unit)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }

    override suspend fun deleteRun(id: RunId) {
        runDao.deleteRun(id.value)
    }
}