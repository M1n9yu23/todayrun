package com.gyugle.gyurun.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run")
internal data class RunEntity(
    @PrimaryKey
    val id: String,
    val durationMillis: Long,
    val dateTimeUtc: String,
    val distanceMeters: Int,
    val latitude: Double,
    val longitude: Double,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val route: String?,
    val mapPicturePath: String?,
    val weatherType: String?,
    val weatherTemperatureCelsius: Double?,
    val weatherFeelsLikeCelsius: Double?,
    val weatherHumidityPercent: Int?,
    val weatherWindSpeedKmh: Double?,
    val steps: Int?,
)