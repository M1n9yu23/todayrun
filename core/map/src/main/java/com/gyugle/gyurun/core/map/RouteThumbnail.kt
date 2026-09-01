package com.gyugle.gyurun.core.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.gyugle.gyurun.core.domain.location.Location
import java.io.ByteArrayOutputStream

private const val THUMBNAIL_WIDTH_PX = 480
private const val THUMBNAIL_HEIGHT_PX = 270
private const val THUMBNAIL_PADDING_PX = 24f
private const val THUMBNAIL_STROKE_PX = 8f
private const val THUMBNAIL_PNG_QUALITY = 100

fun renderRouteThumbnail(route: List<List<Location>>): ByteArray {
    val bitmap =
        Bitmap.createBitmap(
            THUMBNAIL_WIDTH_PX,
            THUMBNAIL_HEIGHT_PX,
            Bitmap.Config.ARGB_8888,
        )
    val canvas = Canvas(bitmap)

    val segments = route.filter { it.size >= 2 }
    if (segments.isNotEmpty()) {
        drawRoute(canvas, segments)
    }

    val stream = ByteArrayOutputStream()
    stream.use { bitmap.compress(Bitmap.CompressFormat.PNG, THUMBNAIL_PNG_QUALITY, it) }
    bitmap.recycle()
    return stream.toByteArray()
}

private fun drawRoute(
    canvas: Canvas,
    segments: List<List<Location>>,
) {
    val projection =
        RouteProjection(
            points = segments.flatten(),
            width = THUMBNAIL_WIDTH_PX.toFloat(),
            height = THUMBNAIL_HEIGHT_PX.toFloat(),
            padding = THUMBNAIL_PADDING_PX,
        )

    val paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = THUMBNAIL_STROKE_PX
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

    segments.forEach { segment ->
        val path = Path()
        segment.forEachIndexed { index, location ->
            val x = projection.toX(location.long)
            val y = projection.toY(location.lat)
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        canvas.drawPath(path, paint)
    }
}
