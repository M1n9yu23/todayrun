package com.gyugle.gyurun.core.database.mapper

import com.gyugle.gyurun.core.database.entity.RunEntity
import com.gyugle.gyurun.core.domain.location.Location
import com.gyugle.gyurun.core.domain.location.LocationTimestamp
import com.gyugle.gyurun.core.domain.location.LocationWithAltitude
import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.RunId
import com.gyugle.gyurun.core.domain.weather.Weather
import com.gyugle.gyurun.core.domain.weather.WeatherType
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

internal fun RunEntity.toRun(): Run =
    Run(
        id = RunId(id),
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = Location(
            lat = latitude,
            long = longitude
        ),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        route = route.toRoute(),
        mapPicturePath = mapPicturePath,
        weather = toWeather(),
        steps = steps
    )

private fun RunEntity.toWeather(): Weather? {
    if (weatherType == null ||
        weatherTemperatureCelsius == null ||
        weatherFeelsLikeCelsius == null ||
        weatherHumidityPercent == null ||
        weatherWindSpeedKmh == null
    ) {
        return null
    }
    return Weather(
        temperatureCelsius = weatherTemperatureCelsius,
        feelsLikeCelsius = weatherFeelsLikeCelsius,
        humidityPercent = weatherHumidityPercent,
        windSpeedKmh = weatherWindSpeedKmh,
        type = WeatherType.entries.find { it.name == weatherType } ?: WeatherType.UNKNOWN,
    )
}

internal fun Run.toRunEntity(mapPicturePath: String?): RunEntity =
    RunEntity(
        id = id?.value ?: UUID.randomUUID().toString(),
        durationMillis = duration.inWholeMilliseconds,
        dateTimeUtc = dateTimeUtc.toInstant().toString(),
        distanceMeters = distanceMeters,
        latitude = location.lat,
        longitude = location.long,
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        route = route.toRouteJson(),
        mapPicturePath = mapPicturePath,
        weatherType = weather?.type?.name,
        weatherTemperatureCelsius = weather?.temperatureCelsius,
        weatherFeelsLikeCelsius = weather?.feelsLikeCelsius,
        weatherHumidityPercent = weather?.humidityPercent,
        weatherWindSpeedKmh = weather?.windSpeedKmh,
        steps = steps,
    )

@Serializable
private data class RoutePointSurrogate(
    val lat: Double,
    val long: Double,
    val altitude: Double,
    val elapsedMillis: Long,
)

private val routeJson = Json { ignoreUnknownKeys = true }

internal fun List<List<LocationTimestamp>>.toRouteJson(): String? {
    if (isEmpty()) return null
    val surrogate =
        map { segment ->
            segment.map { point ->
                RoutePointSurrogate(
                    lat = point.location.location.lat,
                    long = point.location.location.long,
                    altitude = point.location.altitude,
                    elapsedMillis = point.durationTimestamp.inWholeMilliseconds,
                )
            }
        }
    return routeJson.encodeToString(surrogate)
}

internal fun String?.toRoute(): List<List<LocationTimestamp>> {
    if (this == null) return emptyList()
    val surrogate = routeJson.decodeFromString<List<List<RoutePointSurrogate>>>(this)
    return surrogate.map { segment ->
        segment.map { point ->
            LocationTimestamp(
                location =
                    LocationWithAltitude(
                        location = Location(lat = point.lat, long = point.long),
                        altitude = point.altitude,
                    ),
                durationTimestamp = point.elapsedMillis.milliseconds,
            )
        }
    }
}