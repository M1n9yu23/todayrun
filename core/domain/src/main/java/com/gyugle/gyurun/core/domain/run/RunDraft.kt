package com.gyugle.gyurun.core.domain.run

import com.gyugle.gyurun.core.domain.location.LocationTimestamp
import java.time.ZonedDateTime
import kotlin.time.Duration

data class RunDraft(
    val dateTimeUtc: ZonedDateTime,
    val duration: Duration,
    val distanceMeters: Int,
    val route: List<List<LocationTimestamp>> = emptyList(),
    val steps: Int? = null,
)
