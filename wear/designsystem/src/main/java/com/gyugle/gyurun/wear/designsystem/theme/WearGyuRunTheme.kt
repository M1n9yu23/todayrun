package com.gyugle.gyurun.wear.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunBlack
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunDarkErrorContainer
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunDarkSurface
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunDarkSurfaceVariant
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunError
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunErrorDark
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunGray
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunLightErrorContainer
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunOnOrangeContainer
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunOnVioletContainer
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunOrange
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunOrangeContainer
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunOrangeDark
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTeal
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTealDark
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunVioletContainer
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunVioletDim
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunVioletLight
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunWhite
import com.gyugle.gyurun.core.presentation.designsystem.LocalSpacing
import com.gyugle.gyurun.core.presentation.designsystem.Spacing

val WearColorScheme =
    ColorScheme(
        primary = GyuRunVioletLight,
        primaryDim = GyuRunVioletDim,
        primaryContainer = GyuRunVioletContainer,
        onPrimary = GyuRunBlack,
        onPrimaryContainer = GyuRunOnVioletContainer,
        secondary = GyuRunOrange,
        secondaryDim = GyuRunOrangeDark,
        secondaryContainer = GyuRunOrangeContainer,
        onSecondary = GyuRunBlack,
        onSecondaryContainer = GyuRunOnOrangeContainer,
        tertiary = GyuRunTeal,
        tertiaryDim = GyuRunTealDark,
        onTertiary = GyuRunBlack,
        surfaceContainerLow = GyuRunBlack,
        surfaceContainer = GyuRunDarkSurface,
        surfaceContainerHigh = GyuRunDarkSurfaceVariant,
        onSurface = GyuRunWhite,
        onSurfaceVariant = GyuRunGray,
        outline = GyuRunGray,
        outlineVariant = GyuRunDarkSurfaceVariant,
        background = GyuRunBlack,
        onBackground = GyuRunWhite,
        error = GyuRunError,
        errorDim = GyuRunErrorDark,
        errorContainer = GyuRunDarkErrorContainer,
        onError = GyuRunBlack,
        onErrorContainer = GyuRunLightErrorContainer,
    )

@Composable
fun WearGyuRunTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = WearColorScheme,
            typography = WearTypography,
            content = content,
        )
    }
}