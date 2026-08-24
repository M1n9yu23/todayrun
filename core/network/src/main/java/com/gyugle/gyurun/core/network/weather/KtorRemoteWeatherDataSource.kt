package com.gyugle.gyurun.core.network.weather

import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.Result
import com.gyugle.gyurun.core.common.map
import com.gyugle.gyurun.core.domain.location.Location
import com.gyugle.gyurun.core.domain.weather.Weather
import com.gyugle.gyurun.core.network.get
import io.ktor.client.HttpClient
import kotlinx.coroutines.Deferred
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

internal class KtorRemoteWeatherDataSource(
    private val httpClient: Deferred<HttpClient>,
) : RemoteWeatherDataSource {
    override suspend fun getCurrentWeather(location: Location): Result<Weather, DataError.Network> {
        val client =
            try {
                httpClient.await()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "통신 담당자를 짓지 못했다")
                return Result.Error(DataError.Network.UNKNOWN)
            }

        return client
            .get<WeatherResponseDto>(
                route = "/v1/forecast",
                queryParameters =
                    mapOf(
                        "latitude" to location.lat,
                        "longitude" to location.long,
                        "current" to "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m",
                    ),
            ).map { response ->
                response.toWeather()
            }
    }
}