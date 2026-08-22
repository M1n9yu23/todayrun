package com.gyugle.gyurun.core.domain.run

import java.time.LocalDate

data class WeeklyDistance(
    val weekStart: LocalDate,
    val distanceMeters: Int
)
