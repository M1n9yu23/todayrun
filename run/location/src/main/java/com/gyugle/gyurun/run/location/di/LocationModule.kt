package com.gyugle.gyurun.run.location.di

import com.gyugle.gyurun.run.domain.ActiveRunServiceController
import com.gyugle.gyurun.run.domain.LocationObserver
import com.gyugle.gyurun.run.domain.RunningTracker
import com.gyugle.gyurun.run.domain.WatchConnector
import com.gyugle.gyurun.run.location.AndroidActiveRunServiceController
import com.gyugle.gyurun.run.location.AndroidLocationObserver
import com.gyugle.gyurun.run.location.PhoneToWatchConnector
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val locationModule =
    module {
        singleOf(::AndroidLocationObserver) { bind<LocationObserver>() }
        singleOf(::PhoneToWatchConnector) { bind<WatchConnector>() }
        singleOf(::AndroidActiveRunServiceController) { bind<ActiveRunServiceController>() }
        singleOf(::RunningTracker)
    }
