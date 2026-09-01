package com.gyugle.gyurun.core.map

import androidx.compose.runtime.Immutable
import com.gyugle.gyurun.core.domain.location.Location

@Immutable
data class RunMapState(
    val currentLocation: Location? = null,
    val locations: List<List<Location>> = emptyList(),
)
