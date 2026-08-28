package com.gyugle.gyurun.feature.stats.presentation.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyugle.gyurun.core.domain.run.repository.RunRepository
import com.gyugle.gyurun.core.domain.run.usecase.GetRunStatisticsUseCase
import com.gyugle.gyurun.core.domain.run.usecase.GetWeeklyDistancesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class StatsViewModel(
    runRepository: RunRepository,
    getRunStatistics: GetRunStatisticsUseCase,
    getWeeklyDistances: GetWeeklyDistancesUseCase,
) : ViewModel() {
    val state: StateFlow<StatsState> =
        runRepository
            .getRuns()
            .map { runs ->
                val statistics = getRunStatistics(runs)
                if (statistics.totalRuns == 0) {
                    StatsState.Empty
                } else {
                    StatsState.Content(
                        statistics = statistics,
                        weeklyDistances = getWeeklyDistances(runs),
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = StatsState.Loading,
            )
}