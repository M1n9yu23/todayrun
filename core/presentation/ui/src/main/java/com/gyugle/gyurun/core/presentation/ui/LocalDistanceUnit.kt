package com.gyugle.gyurun.core.presentation.ui

import androidx.compose.runtime.compositionLocalOf
import com.gyugle.gyurun.core.domain.run.DistanceUnit

val LocalDistanceUnit = compositionLocalOf { DistanceUnit.KILOMETERS }
