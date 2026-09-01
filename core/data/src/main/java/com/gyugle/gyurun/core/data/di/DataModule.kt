package com.gyugle.gyurun.core.data.di

import com.gyugle.gyurun.core.data.run.DefaultRunDraftRepository
import com.gyugle.gyurun.core.data.run.InternalRunMapStorage
import com.gyugle.gyurun.core.data.run.OfflineFirstRunRepository
import com.gyugle.gyurun.core.data.run.RunMapStorage
import com.gyugle.gyurun.core.data.run.WeatherBackfillWorker
import com.gyugle.gyurun.core.data.run.WorkManagerWeatherBackfillScheduler
import com.gyugle.gyurun.core.data.weather.DefaultWeatherRepository
import com.gyugle.gyurun.core.domain.run.repository.RunDraftRepository
import com.gyugle.gyurun.core.domain.run.repository.RunRepository
import com.gyugle.gyurun.core.domain.run.repository.WeatherBackfillScheduler
import com.gyugle.gyurun.core.domain.weather.repository.WeatherRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val dataModule =
    module {
        single<RunMapStorage> { InternalRunMapStorage(androidApplication().filesDir, get()) }
        single<WeatherBackfillScheduler> { WorkManagerWeatherBackfillScheduler(androidApplication()) }
        single<RunRepository> { OfflineFirstRunRepository(get(), get(), get(), get()) }
        single<RunDraftRepository> { DefaultRunDraftRepository(get()) }
        single<WeatherRepository> { DefaultWeatherRepository(get()) }
        worker { params ->
            WeatherBackfillWorker(
                context = androidApplication(),
                params = params.get(),
                runRepository = get(),
                weatherRepository = get(),
            )
        }
    }
