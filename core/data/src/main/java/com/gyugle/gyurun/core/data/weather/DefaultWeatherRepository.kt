package com.gyugle.gyurun.core.data.weather

import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.Result
import com.gyugle.gyurun.core.domain.location.Location
import com.gyugle.gyurun.core.domain.weather.Weather
import com.gyugle.gyurun.core.domain.weather.repository.WeatherRepository
import com.gyugle.gyurun.core.network.weather.RemoteWeatherDataSource

internal class DefaultWeatherRepository(
    private val remoteWeatherDataSource: RemoteWeatherDataSource,
) : WeatherRepository {
    override suspend fun getCurrentWeather(location: Location): Result<Weather, DataError.Network> =
        remoteWeatherDataSource.getCurrentWeather(location)
}
