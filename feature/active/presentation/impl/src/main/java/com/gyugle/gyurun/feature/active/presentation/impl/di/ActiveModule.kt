package com.gyugle.gyurun.feature.active.presentation.impl.di

import com.gyugle.gyurun.core.navigation.Navigator
import com.gyugle.gyurun.feature.active.presentation.api.ActiveRunNavKey
import com.gyugle.gyurun.feature.active.presentation.impl.ActiveRunScreenRoot
import com.gyugle.gyurun.feature.active.presentation.impl.ActiveRunViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val activePresentationModule =
    module {
        viewModelOf(::ActiveRunViewModel)
        navigation<ActiveRunNavKey> {
            val navigator = get<Navigator>()
            ActiveRunScreenRoot(
                onExit = { navigator.navigateBack() },
            )
        }
    }
