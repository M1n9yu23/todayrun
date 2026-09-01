package com.gyugle.gyurun.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gyugle.gyurun.core.database.entity.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface RunDao {
    @Query("SELECT * FROM run ORDER BY dateTimeUtc DESC")
    fun getRuns(): Flow<List<RunEntity>>

    @Query("SELECT * FROM run WHERE dateTimeUtc > :sinceUtc ORDER BY dateTimeUtc DESC")
    fun getRunsSince(sinceUtc: String): Flow<List<RunEntity>>

    @Query("SELECT * FROM run ORDER BY dateTimeUtc DESC LIMIT 1")
    fun getMostRecentRun(): Flow<RunEntity?>

    @Query("SELECT * FROM run ORDER BY dateTimeUtc DESC")
    fun getRunsPaged(): PagingSource<Int, RunEntity>

    @Query("SELECT * FROM run WHERE id = :id")
    fun getRun(id: String): Flow<RunEntity?>

    @Upsert
    suspend fun upsertRun(run: RunEntity)

    @Query(
        "UPDATE run SET " +
            "weatherType = :weatherType, " +
            "weatherTemperatureCelsius = :temperatureCelsius, " +
            "weatherFeelsLikeCelsius = :feelsLikeCelsius, " +
            "weatherHumidityPercent = :humidityPercent, " +
            "weatherWindSpeedKmh = :windSpeedKmh " +
            "WHERE id = :id",
    )
    suspend fun updateWeather(
        id: String,
        weatherType: String,
        temperatureCelsius: Double,
        feelsLikeCelsius: Double,
        humidityPercent: Int,
        windSpeedKmh: Double,
    )

    @Query("DELETE FROM run WHERE id = :id")
    suspend fun deleteRun(id: String)
}
