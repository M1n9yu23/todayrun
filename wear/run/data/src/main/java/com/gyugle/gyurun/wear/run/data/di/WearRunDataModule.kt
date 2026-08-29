package com.gyugle.gyurun.wear.run.data.di

import com.gyugle.gyurun.wear.run.data.HealthServicesExerciseTracker
import com.gyugle.gyurun.wear.run.data.WatchToPhoneConnector
import com.gyugle.gyurun.wear.run.domain.ExerciseTracker
import com.gyugle.gyurun.wear.run.domain.PhoneConnector
import com.gyugle.gyurun.wear.run.domain.RestartLogger
import com.gyugle.gyurun.wear.run.domain.RunningTracker
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import timber.log.Timber

val wearRunDataModule =
    module {
        singleOf(::HealthServicesExerciseTracker) { bind<ExerciseTracker>() }
        singleOf(::WatchToPhoneConnector) { bind<PhoneConnector>() }
        single<RestartLogger> {
            RestartLogger { pipe, cause, attempt ->
                Timber.w(cause, "%s 통로가 끊겨 다시 잇는다(%d 번째 실패)", pipe, attempt + 1)
            }
        }
        singleOf(::RunningTracker)
    }