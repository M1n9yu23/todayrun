package com.gyugle.gyurun.core.common

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val FIRST_DELAY = 1.seconds

private val MAX_DELAY = 32.seconds

private const val DOUBLINGS_TO_MAX = 5L

fun restartDelay(attempt: Long): Duration =
    if (attempt >= DOUBLINGS_TO_MAX) {
        MAX_DELAY
    } else {
        FIRST_DELAY * (1 shl attempt.toInt())
    }
