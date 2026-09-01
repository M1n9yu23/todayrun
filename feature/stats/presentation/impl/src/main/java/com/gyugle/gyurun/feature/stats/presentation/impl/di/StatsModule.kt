package com.gyugle.gyurun.feature.stats.presentation.impl.di

import com.gyugle.gyurun.core.domain.run.usecase.GetRunStatisticsUseCase
import com.gyugle.gyurun.core.domain.run.usecase.GetWeeklyDistancesUseCase
import com.gyugle.gyurun.core.navigation.Navigator
import com.gyugle.gyurun.feature.stats.presentation.api.StatsNavKey
import com.gyugle.gyurun.feature.stats.presentation.impl.StatsScreenRoot
import com.gyugle.gyurun.feature.stats.presentation.impl.StatsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val statsPresentationModule =
    module {
        singleOf(::GetRunStatisticsUseCase)
        singleOf(::GetWeeklyDistancesUseCase)
        viewModelOf(::StatsViewModel)
        navigation<StatsNavKey> {
            val navigator = get<Navigator>()
            StatsScreenRoot(
                onBackClick = { navigator.navigateBack() },
            )
        }
    }
