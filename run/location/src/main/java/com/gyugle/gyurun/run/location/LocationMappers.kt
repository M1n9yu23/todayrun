package com.gyugle.gyurun.run.location

import android.location.Location
import com.gyugle.gyurun.core.domain.location.LocationWithAltitude

internal fun Location.toLocationWithAltitude(): LocationWithAltitude =
    LocationWithAltitude(
        location =
            com.gyugle.gyurun.core.domain.location.Location(
                lat = latitude,
                long = longitude,
            ),
        altitude = altitude,
    )