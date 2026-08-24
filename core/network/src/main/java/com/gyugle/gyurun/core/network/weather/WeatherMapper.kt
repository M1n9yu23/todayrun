package com.gyugle.gyurun.core.network.weather

import com.gyugle.gyurun.core.domain.weather.Weather
import com.gyugle.gyurun.core.domain.weather.WeatherType

fun WeatherResponseDto.toWeather(): Weather =
    Weather(
        temperatureCelsius = current.temperatureCelsius,
        feelsLikeCelsius = current.feelsLikeCelsius,
        humidityPercent = current.humidityPercent,
        windSpeedKmh = current.windSpeedKmh,
        type = current.weatherCode.toWeatherType(),
    )

fun Int.toWeatherType(): WeatherType =
    when (this) {
        0, 1 -> WeatherType.CLEAR
        2, 3 -> WeatherType.CLOUDY
        45, 48 -> WeatherType.FOG
        in 51..67, in 80..82 -> WeatherType.RAIN
        in 71..77, in 85..86 -> WeatherType.SNOW
        in 95..99 -> WeatherType.THUNDERSTORM
        else -> WeatherType.UNKNOWN
    }