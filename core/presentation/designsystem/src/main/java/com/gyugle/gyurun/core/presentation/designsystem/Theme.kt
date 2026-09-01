package com.gyugle.gyurun.core.presentation.designsystem

import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

val GyuRunDarkColorScheme =
    darkColorScheme(
        primary = GyuRunVioletLight,
        onPrimary = GyuRunBlack,
        primaryContainer = GyuRunVioletContainer,
        onPrimaryContainer = GyuRunOnVioletContainer,
        secondary = GyuRunOrange,
        onSecondary = GyuRunBlack,
        secondaryContainer = GyuRunOrangeContainer,
        onSecondaryContainer = GyuRunOnOrangeContainer,
        tertiary = GyuRunTeal,
        onTertiary = GyuRunBlack,
        background = GyuRunBlack,
        onBackground = GyuRunWhite,
        surface = GyuRunDarkSurface,
        onSurface = GyuRunWhite,
        surfaceVariant = GyuRunDarkSurfaceVariant,
        onSurfaceVariant = GyuRunGray,
        outline = GyuRunDarkOutline,
        error = GyuRunError,
        onError = GyuRunBlack,
        errorContainer = GyuRunDarkErrorContainer,
        onErrorContainer = GyuRunLightErrorContainer,
    )

val GyuRunLightColorScheme =
    lightColorScheme(
        primary = GyuRunVioletDark,
        onPrimary = GyuRunWhite,
        primaryContainer = GyuRunLightVioletContainer,
        onPrimaryContainer = GyuRunOnLightVioletContainer,
        secondary = GyuRunOrangeDark,
        onSecondary = GyuRunInk,
        secondaryContainer = GyuRunLightOrangeContainer,
        onSecondaryContainer = GyuRunOnLightOrangeContainer,
        tertiary = GyuRunTealDark,
        onTertiary = GyuRunInk,
        background = GyuRunLightBackground,
        onBackground = GyuRunInk,
        surface = GyuRunLightSurface,
        onSurface = GyuRunInk,
        surfaceVariant = GyuRunLightSurfaceVariant,
        onSurfaceVariant = GyuRunLightGray,
        outline = GyuRunLightOutline,
        error = GyuRunErrorDark,
        onError = GyuRunWhite,
        errorContainer = GyuRunLightErrorContainer,
        onErrorContainer = GyuRunOnLightErrorContainer,
    )

@Composable
fun GyuRunTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) GyuRunDarkColorScheme else GyuRunLightColorScheme
    val reduceMotion = rememberReduceMotion()
    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalMotion provides Motion(reduceMotion = reduceMotion),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = GyuRunShapes,
            content = content,
        )
    }
}

@Composable
private fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        val scale =
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            )
        scale == 0f
    }
}
