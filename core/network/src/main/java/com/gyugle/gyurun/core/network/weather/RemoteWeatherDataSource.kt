package com.gyugle.gyurun.core.network.weather

import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.Result
import com.gyugle.gyurun.core.domain.location.Location
import com.gyugle.gyurun.core.domain.weather.Weather

interface RemoteWeatherDataSource {
    suspend fun getCurrentWeather(location: Location): Result<Weather, DataError.Network>
}