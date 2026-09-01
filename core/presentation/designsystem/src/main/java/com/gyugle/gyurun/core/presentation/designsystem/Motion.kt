package com.gyugle.gyurun.core.presentation.designsystem

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

data class Motion(
    val reduceMotion: Boolean = false,
    val durationShort: Int = 150,
    val durationMedium: Int = 250,
    val durationLong: Int = 400,
    val standardEasing: Easing = FastOutSlowInEasing,
    val emphasizedEasing: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f),
) {
    val contentEnter: EnterTransition
        get() =
            if (reduceMotion) {
                fadeIn(tween(durationShort, easing = standardEasing))
            } else {
                fadeIn(tween(durationMedium, easing = standardEasing)) +
                    slideInVertically(
                        tween(
                            durationMedium,
                            easing = emphasizedEasing,
                        ),
                    ) { height -> height / 6 }
            }

    val contentExit: ExitTransition
        get() = fadeOut(tween(durationShort, easing = standardEasing))

    val growSpec: AnimationSpec<Float>
        get() =
            if (reduceMotion) {
                snap()
            } else {
                tween(durationLong, easing = emphasizedEasing)
            }
}

val LocalMotion = staticCompositionLocalOf { Motion() }

val MaterialTheme.motion: Motion
    @Composable
    @ReadOnlyComposable
    get() = LocalMotion.current
