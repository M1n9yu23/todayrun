@file:OptIn(MapsComposeExperimentalApi::class)

package com.gyugle.gyurun.core.map

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

private const val LIVE_TRACKING_ZOOM = 17f
private const val ROUTE_BOUNDS_PADDING = 100

private val LocationMarkerSize = 26.dp
private val LocationMarkerCoreSize = 16.dp
private val MarkerPreviewPadding = 24.dp

@Composable
fun MapView(
    state: RunMapState,
    modifier: Modifier = Modifier,
) {
    val cameraPositionState = rememberCameraPositionState()

    val currentLocation = state.currentLocation
    LaunchedEffect(currentLocation) {
        if (currentLocation != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(currentLocation.lat, currentLocation.long),
                    LIVE_TRACKING_ZOOM,
                ),
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = remember { MapUiSettings(zoomControlsEnabled = false) },
    ) {
        state.locations.forEach { segment ->
            Polyline(
                points = segment.map { LatLng(it.lat, it.long) },
                color = MaterialTheme.colorScheme.primary,
                jointType = JointType.ROUND,
            )
        }

        if (currentLocation != null) {
            MarkerComposable(
                currentLocation,
                state =
                    rememberUpdatedMarkerState(
                        LatLng(currentLocation.lat, currentLocation.long),
                    ),
            ) {
                CurrentLocationMarker()
            }
        } else {
            MapEffect(state.locations) { map ->
                val points = state.locations.flatten()
                if (points.isNotEmpty()) {
                    val bounds =
                        LatLngBounds
                            .builder()
                            .apply {
                                points.forEach { include(LatLng(it.lat, it.long)) }
                            }.build()
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngBounds(bounds, ROUTE_BOUNDS_PADDING),
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentLocationMarker() {
    Box(
        modifier =
            Modifier
                .size(LocationMarkerSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(LocationMarkerCoreSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
        )
    }
}

@PreviewLightDark
@Composable
private fun CurrentLocationMarkerPreview() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            Box(modifier = Modifier.padding(MarkerPreviewPadding)) {
                CurrentLocationMarker()
            }
        }
    }
}