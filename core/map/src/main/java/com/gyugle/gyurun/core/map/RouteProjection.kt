package com.gyugle.gyurun.core.map

import com.gyugle.gyurun.core.domain.location.Location
import kotlin.math.cos

internal class RouteProjection(
    points: List<Location>,
    width: Float,
    height: Float,
    padding: Float,
) {
    private val minLat = points.minOf { it.lat }
    private val maxLat = points.maxOf { it.lat }
    private val longScale = cos(Math.toRadians((minLat + maxLat) / 2.0))
    private val minProjectedX = points.minOf { it.long } * longScale
    private val spanX = points.maxOf { it.long } * longScale - minProjectedX
    private val spanY = maxLat - minLat

    private val drawableWidth = width - padding * 2
    private val drawableHeight = height - padding * 2
    private val scale = fitScale(spanX, spanY, drawableWidth, drawableHeight)
    private val offsetX = padding + (drawableWidth - spanX * scale) / 2.0
    private val offsetY = padding + (drawableHeight - spanY * scale) / 2.0

    fun toX(long: Double): Float = (offsetX + (long * longScale - minProjectedX) * scale).toFloat()

    fun toY(lat: Double): Float = (offsetY + (maxLat - lat) * scale).toFloat()
}

private fun fitScale(
    spanX: Double,
    spanY: Double,
    drawableWidth: Float,
    drawableHeight: Float,
): Double {
    val scaleX = if (spanX > 0.0) drawableWidth / spanX else Double.POSITIVE_INFINITY
    val scaleY = if (spanY > 0.0) drawableHeight / spanY else Double.POSITIVE_INFINITY
    val fitted = minOf(scaleX, scaleY)
    return if (fitted.isFinite()) fitted else 1.0
}
