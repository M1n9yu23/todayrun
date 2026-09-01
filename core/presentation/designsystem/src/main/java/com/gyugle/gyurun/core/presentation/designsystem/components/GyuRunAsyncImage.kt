package com.gyugle.gyurun.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.spacing

@Composable
fun GyuRunAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    contentScale: ContentScale = ContentScale.Crop,
    colorFilter: ColorFilter? = null,
) {
    AsyncImage(
        model =
            ImageRequest
                .Builder(LocalPlatformContext.current)
                .data(model)
                .crossfade(true)
                .build(),
        contentDescription = contentDescription,
        modifier =
            modifier
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentScale = contentScale,
        colorFilter = colorFilter,
    )
}

@PreviewLightDark
@Composable
private fun GyuRunAsyncImagePreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GyuRunAsyncImage(
                model = null,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.spacing.huge),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun GyuRunAsyncImageTintedPreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GyuRunAsyncImage(
                model = null,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.spacing.huge),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            )
        }
    }
}
