package com.gyugle.gyurun.run.domain

import com.gyugle.gyurun.core.domain.location.LocationWithAltitude
import kotlinx.coroutines.flow.Flow

interface LocationObserver {
    val isLocationAvailable: Flow<Boolean>

    fun observeLocation(interval: Long): Flow<LocationWithAltitude>
}
