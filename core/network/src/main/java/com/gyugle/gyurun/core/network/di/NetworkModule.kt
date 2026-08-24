package com.gyugle.gyurun.core.network.di

import com.gyugle.gyurun.core.common.DispatcherProvider
import com.gyugle.gyurun.core.network.HttpClientFactory
import com.gyugle.gyurun.core.network.weather.KtorRemoteWeatherDataSource
import com.gyugle.gyurun.core.network.weather.RemoteWeatherDataSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import org.koin.dsl.module

val networkModule =
    module {
        single<Deferred<HttpClient>> {
            get<CoroutineScope>().async(get<DispatcherProvider>().io) {
                HttpClientFactory().build(CIO.create())
            }
        }
        single<RemoteWeatherDataSource> { KtorRemoteWeatherDataSource(get()) }
    }