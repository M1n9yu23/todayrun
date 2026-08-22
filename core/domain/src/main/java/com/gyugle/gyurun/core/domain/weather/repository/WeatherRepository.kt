package com.gyugle.gyurun.core.domain.weather.repository

import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.Result
import com.gyugle.gyurun.core.domain.location.Location
import com.gyugle.gyurun.core.domain.weather.Weather

interface WeatherRepository {
    suspend fun getCurrentWeather(location: Location): Result<Weather, DataError.Network>
}