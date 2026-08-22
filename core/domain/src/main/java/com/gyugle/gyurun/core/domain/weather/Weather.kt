package com.gyugle.gyurun.core.domain.weather

data class Weather(
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val type: WeatherType
)
