package com.gyugle.gyurun.run.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.gyugle.gyurun.core.domain.location.LocationWithAltitude
import com.gyugle.gyurun.run.domain.LocationObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.time.Duration.Companion.seconds

internal class AndroidLocationObserver(
    private val context: Context,
) : LocationObserver {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    private val _isLocationAvailable = MutableStateFlow(true)
    override val isLocationAvailable: Flow<Boolean> = _isLocationAvailable.asStateFlow()

    override fun observeLocation(interval: Long): Flow<LocationWithAltitude> {
        return callbackFlow {
            val locationManager =
                context.getSystemService<LocationManager>() ?: run {
                    close()
                    return@callbackFlow
                }

            var isGpsEnabled = false
            var isNetworkEnabled = false
            while (!isGpsEnabled && !isNetworkEnabled) {
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if (!isGpsEnabled && !isNetworkEnabled) {
                    delay(3000L)
                }
            }

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                close()
            } else {
                client.lastLocation.addOnSuccessListener { location ->
                    location?.takeIf { it.isRecentEnough() }?.let {
                        trySend(it.toLocationWithAltitude())
                    }
                }

                val request =
                    LocationRequest
                        .Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
                        .build()

                val locationCallback =
                    object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            super.onLocationResult(result)
                            result.locations.lastOrNull()?.let { location ->
                                trySend(location.toLocationWithAltitude())
                            }
                        }

                        override fun onLocationAvailability(availability: LocationAvailability) {
                            super.onLocationAvailability(availability)
                            _isLocationAvailable.value = availability.isLocationAvailable
                        }
                    }

                client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

                awaitClose {
                    client.removeLocationUpdates(locationCallback)
                    _isLocationAvailable.value = true
                }
            }
        }
    }
}

private val MAX_LAST_LOCATION_AGE_NANOS = 10.seconds.inWholeNanoseconds

internal fun isRecentEnough(
    nowNanos: Long,
    fixNanos: Long,
): Boolean = nowNanos - fixNanos <= MAX_LAST_LOCATION_AGE_NANOS

private fun Location.isRecentEnough(): Boolean =
    isRecentEnough(SystemClock.elapsedRealtimeNanos(), elapsedRealtimeNanos)