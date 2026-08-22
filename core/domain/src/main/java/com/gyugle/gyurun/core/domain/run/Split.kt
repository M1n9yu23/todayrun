package com.gyugle.gyurun.core.domain.run

import kotlin.time.Duration

// 1킬로미터마다 끊을 타입.
data class Split(
    val distanceMeters: Int,
    val duration: Duration,
    val elevationGainMeters: Int,
)