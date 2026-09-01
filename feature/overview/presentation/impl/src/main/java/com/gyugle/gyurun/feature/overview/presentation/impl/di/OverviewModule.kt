package com.gyugle.gyurun.feature.overview.presentation.impl.di

import com.gyugle.gyurun.core.navigation.Navigator
import com.gyugle.gyurun.feature.active.presentation.api.ActiveRunNavKey
import com.gyugle.gyurun.feature.details.presentation.api.RunDetailNavKey
import com.gyugle.gyurun.feature.overview.presentation.api.OverviewNavKey
import com.gyugle.gyurun.feature.overview.presentation.impl.OverviewScreenRoot
import com.gyugle.gyurun.feature.overview.presentation.impl.OverviewViewModel
import com.gyugle.gyurun.feature.settings.presentation.api.SettingsNavKey
import com.gyugle.gyurun.feature.stats.presentation.api.StatsNavKey
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val overviewPresentationModule =
    module {
        viewModelOf(::OverviewViewModel)
        navigation<OverviewNavKey> {
            val navigator = get<Navigator>()
            OverviewScreenRoot(
                onStartRun = { navigator.navigateTo(ActiveRunNavKey) },
                onOpenRun = { runId -> navigator.navigateTo(RunDetailNavKey(runId)) },
                onOpenStats = { navigator.navigateTo(StatsNavKey) },
                onOpenSettings = { navigator.navigateTo(SettingsNavKey) },
            )
        }
    }
