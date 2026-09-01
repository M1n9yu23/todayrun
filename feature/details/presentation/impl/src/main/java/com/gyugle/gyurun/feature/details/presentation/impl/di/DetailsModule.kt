package com.gyugle.gyurun.feature.details.presentation.impl.di

import com.gyugle.gyurun.core.navigation.Navigator
import com.gyugle.gyurun.feature.details.presentation.api.RunDetailNavKey
import com.gyugle.gyurun.feature.details.presentation.impl.RunDetailScreenRoot
import com.gyugle.gyurun.feature.details.presentation.impl.RunDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val detailsPresentationModule =
    module {
        viewModel { (runId: String) ->
            RunDetailViewModel(runId = runId, runRepository = get())
        }
        navigation<RunDetailNavKey> { key ->
            val navigator = get<Navigator>()
            RunDetailScreenRoot(
                runId = key.runId,
                onBackClick = { navigator.navigateBack() },
            )
        }
    }
