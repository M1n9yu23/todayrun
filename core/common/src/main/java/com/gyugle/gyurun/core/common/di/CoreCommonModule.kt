package com.gyugle.gyurun.core.common.di

import com.gyugle.gyurun.core.common.DispatcherProvider
import com.gyugle.gyurun.core.common.StandardDispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val coreCommonModule =
    module {
        single<DispatcherProvider> { StandardDispatcherProvider() }

        single<CoroutineScope> {
            CoroutineScope(SupervisorJob() + get<DispatcherProvider>().default)
        }
    }