package com.gyugle.gyurun.core.domain.run

import com.gyugle.gyurun.core.domain.location.Location
import com.gyugle.gyurun.core.domain.location.LocationTimestamp
import com.gyugle.gyurun.core.domain.weather.Weather
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

data class Run(
    val id: RunId?,
    val duration: Duration,
    val dateTimeUtc: ZonedDateTime,
    val distanceMeters: Int,
    val location: Location,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val route: List<List<LocationTimestamp>> = emptyList(),
    val mapPicturePath: String? = null,
    val weather: Weather? = null,
    val steps: Int? = null,
) {
    val avgSpeedKmh: Double
        get() = (distanceMeters / 1000.0) / duration.toDouble(DurationUnit.HOURS)
}
