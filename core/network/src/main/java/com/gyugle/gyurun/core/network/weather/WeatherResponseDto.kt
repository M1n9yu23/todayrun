package com.gyugle.gyurun.core.network.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    val current: CurrentWeatherDto,
)

@Serializable
data class CurrentWeatherDto(
    @SerialName("temperature_2m") val temperatureCelsius: Double,
    @SerialName("apparent_temperature") val feelsLikeCelsius: Double,
    @SerialName("relative_humidity_2m") val humidityPercent: Int,
    @SerialName("wind_speed_10m") val windSpeedKmh: Double,
    @SerialName("weather_code") val weatherCode: Int,
)
