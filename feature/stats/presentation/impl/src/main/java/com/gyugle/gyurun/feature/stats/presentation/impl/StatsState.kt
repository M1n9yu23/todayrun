package com.gyugle.gyurun.feature.stats.presentation.impl

import com.gyugle.gyurun.core.domain.run.RunStatistics
import com.gyugle.gyurun.core.domain.run.WeeklyDistance

internal sealed interface StatsState {
    data object Loading : StatsState

    data object Empty : StatsState

    data class Content(
        val statistics: RunStatistics,
        val weeklyDistances: List<WeeklyDistance>,
    ) : StatsState
}