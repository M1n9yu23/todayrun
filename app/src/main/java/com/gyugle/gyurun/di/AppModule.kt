package com.gyugle.gyurun.di

import com.gyugle.gyurun.MainViewModel
import com.gyugle.gyurun.core.navigation.Navigator
import com.gyugle.gyurun.reminder.WeeklySummaryWorker
import com.gyugle.gyurun.widget.WidgetLanguage
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule =
    module {
        single { Navigator() }
        single { WidgetLanguage() }
        viewModelOf(::MainViewModel)
        worker { params ->
            WeeklySummaryWorker(
                context = androidApplication(),
                params = params.get(),
                runRepository = get(),
                userSettingsRepository = get()
            )
        }
    }