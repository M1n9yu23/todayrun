package com.gyugle.gyurun.core.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.gyugle.gyurun.core.domain.location.Location
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet
import timber.log.Timber

private const val LIVE_TRACKING_ZOOM = 17
private const val ROUTE_BOUNDS_PADDING = 100
private const val ROUTE_LINE_WIDTH_PX = 16f

private const val MARKER_SIZE_DP = 26
private const val MARKER_CORE_RATIO = 16f / 26f

private val NoticePadding = 24.dp

@Composable
fun MapView(
    state: RunMapState,
    modifier: Modifier = Modifier,
) {
    if (BuildConfig.KAKAO_APP_KEY.isBlank()) {
        MissingKeyNotice(modifier = modifier)
    } else {
        KakaoMapView(state = state, modifier = modifier)
    }
}

@Composable
private fun MissingKeyNotice(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.map_kakao_key_missing),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(NoticePadding),
        )
    }
}

@Composable
private fun KakaoMapView(
    state: RunMapState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val routeColor = MaterialTheme.colorScheme.primary.toArgb()
    val markerCoreColor = MaterialTheme.colorScheme.primary.toArgb()
    val markerRingColor = MaterialTheme.colorScheme.surface.toArgb()
    val density = context.resources.displayMetrics.density
    val markerBitmap =
        remember(markerCoreColor, markerRingColor, density) {
            currentLocationMarker(density, markerCoreColor, markerRingColor)
        }

    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }

    val mapView =
        remember {
            ensureKakaoSdkInitialized(context)
            MapView(context).apply {
                setFinishManually(true)
                start(
                    object : MapLifeCycleCallback() {
                        override fun onMapDestroy() = Unit

                        override fun onMapError(error: Exception) {
                            Timber.e(error, "카카오 지도를 준비하지 못했다")
                        }
                    },
                    object : KakaoMapReadyCallback() {
                        override fun onMapReady(map: KakaoMap) {
                            kakaoMap = map
                        }
                    },
                )
            }
        }

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> mapView.resume()
                    Lifecycle.Event.ON_PAUSE -> mapView.pause()
                    else -> Unit
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.finish()
        }
    }

    LaunchedEffect(kakaoMap, state, routeColor, markerBitmap) {
        val map = kakaoMap ?: return@LaunchedEffect
        map.drawRoute(state.locations, routeColor)
        map.drawCurrentLocation(state.currentLocation, markerBitmap)
        map.moveCameraTo(state)
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
    )
}

private var kakaoSdkInitialized = false

private fun ensureKakaoSdkInitialized(context: Context) {
    if (!kakaoSdkInitialized) {
        KakaoMapSdk.init(context.applicationContext, BuildConfig.KAKAO_APP_KEY)
        kakaoSdkInitialized = true
    }
}

private fun KakaoMap.drawRoute(
    segments: List<List<Location>>,
    color: Int,
) {
    val layer = routeLineManager?.layer ?: return
    layer.removeAll()

    val stylesSet =
        RouteLineStylesSet.from(
            "paceRoute",
            RouteLineStyles.from(RouteLineStyle.from(ROUTE_LINE_WIDTH_PX, color)),
        )
    segments.forEach { segment ->
        if (segment.size < 2) return@forEach
        val points = segment.map { LatLng.from(it.lat, it.long) }
        val routeSegment = RouteLineSegment.from(points).setStyles(stylesSet.getStyles(0))
        layer.addRouteLine(RouteLineOptions.from(routeSegment).setStylesSet(stylesSet))
    }
}

private fun KakaoMap.drawCurrentLocation(
    location: Location?,
    markerBitmap: Bitmap,
) {
    val manager = labelManager ?: return
    val layer = manager.layer ?: return
    layer.removeAll()
    if (location == null) return

    val styles = manager.addLabelStyles(LabelStyles.from(LabelStyle.from(markerBitmap)))
    layer.addLabel(
        LabelOptions.from(LatLng.from(location.lat, location.long)).setStyles(styles),
    )
}

private fun KakaoMap.moveCameraTo(state: RunMapState) {
    val current = state.currentLocation
    if (current != null) {
        moveCamera(
            CameraUpdateFactory.newCenterPosition(
                LatLng.from(current.lat, current.long),
                LIVE_TRACKING_ZOOM,
            ),
        )
    } else {
        val points = state.locations.flatten()
        if (points.isNotEmpty()) {
            val latLngs = points.map { LatLng.from(it.lat, it.long) }.toTypedArray()
            moveCamera(CameraUpdateFactory.fitMapPoints(latLngs, ROUTE_BOUNDS_PADDING))
        }
    }
}

private fun currentLocationMarker(
    density: Float,
    coreColor: Int,
    ringColor: Int,
): Bitmap {
    val size = (MARKER_SIZE_DP * density).toInt().coerceAtLeast(1)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val center = size / 2f
    canvas.drawCircle(
        center,
        center,
        center,
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ringColor },
    )
    canvas.drawCircle(
        center,
        center,
        center * MARKER_CORE_RATIO,
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = coreColor },
    )
    return bitmap
}
