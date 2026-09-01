package com.gyugle.gyurun.run.sensor.di

import com.gyugle.gyurun.run.domain.StepObserver
import com.gyugle.gyurun.run.sensor.AndroidStepObserver
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val sensorModule =
    module {
        single<StepObserver> { AndroidStepObserver(androidApplication()) }
    }
