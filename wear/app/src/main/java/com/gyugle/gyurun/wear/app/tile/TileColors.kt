package com.gyugle.gyurun.wear.app.tile

import androidx.compose.ui.graphics.toArgb
import androidx.wear.protolayout.material3.ColorScheme
import androidx.wear.protolayout.types.argb
import androidx.wear.compose.material3.ColorScheme as ComposeColorScheme

internal fun tileColorScheme(theme: ComposeColorScheme): ColorScheme =
    ColorScheme(
        primary = theme.primary.toArgb().argb,
        primaryDim = theme.primaryDim.toArgb().argb,
        primaryContainer = theme.primaryContainer.toArgb().argb,
        onPrimary = theme.onPrimary.toArgb().argb,
        onPrimaryContainer = theme.onPrimaryContainer.toArgb().argb,
        secondary = theme.secondary.toArgb().argb,
        secondaryDim = theme.secondaryDim.toArgb().argb,
        secondaryContainer = theme.secondaryContainer.toArgb().argb,
        onSecondary = theme.onSecondary.toArgb().argb,
        onSecondaryContainer = theme.onSecondaryContainer.toArgb().argb,
        tertiary = theme.tertiary.toArgb().argb,
        tertiaryDim = theme.tertiaryDim.toArgb().argb,
        tertiaryContainer = theme.tertiaryContainer.toArgb().argb,
        onTertiary = theme.onTertiary.toArgb().argb,
        onTertiaryContainer = theme.onTertiaryContainer.toArgb().argb,
        surfaceContainerLow = theme.surfaceContainerLow.toArgb().argb,
        surfaceContainer = theme.surfaceContainer.toArgb().argb,
        surfaceContainerHigh = theme.surfaceContainerHigh.toArgb().argb,
        onSurface = theme.onSurface.toArgb().argb,
        onSurfaceVariant = theme.onSurfaceVariant.toArgb().argb,
        outline = theme.outline.toArgb().argb,
        outlineVariant = theme.outlineVariant.toArgb().argb,
        background = theme.background.toArgb().argb,
        onBackground = theme.onBackground.toArgb().argb,
        error = theme.error.toArgb().argb,
        errorDim = theme.errorDim.toArgb().argb,
        errorContainer = theme.errorContainer.toArgb().argb,
        onError = theme.onError.toArgb().argb,
        onErrorContainer = theme.onErrorContainer.toArgb().argb,
    )
