package com.gyugle.gyurun.run.domain

import com.gyugle.gyurun.core.domain.location.LocationTimestamp
import kotlin.time.Duration

data class RunData(
    val distanceMeters: Int = 0,
    val pace: Duration = Duration.ZERO,
    val locations: List<List<LocationTimestamp>> = emptyList(),
    val steps: Int? = null,
    val heartRate: Int = 0,
    val isLocationAvailable: Boolean = true,
)
