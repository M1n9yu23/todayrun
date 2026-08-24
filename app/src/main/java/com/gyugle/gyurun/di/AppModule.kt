package com.gyugle.gyurun.di

import com.gyugle.gyurun.MainViewModel
import com.gyugle.gyurun.core.navigation.Navigator
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule =
    module {
        single { Navigator() }
        viewModelOf(::MainViewModel)
    }