package com.gyugle.gyurun.core.presentation.designsystem.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.spacing

@Composable
fun GyuRunSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 900),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "skeleton-alpha",
    )
    Box(
        modifier =
            modifier
                .clip(shape)
                .graphicsLayer { this.alpha = alpha }
                .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

@Composable
fun GyuRunListLoading(
    modifier: Modifier = Modifier,
    itemCount: Int = 4,
) {
    Column(
        modifier = modifier.padding(MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        repeat(itemCount) {
            GyuRunCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                ) {
                    GyuRunSkeleton(
                        modifier =
                            Modifier
                                .fillMaxWidth(fraction = 0.5f)
                                .height(MaterialTheme.spacing.medium),
                    )
                    GyuRunSkeleton(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(MaterialTheme.spacing.large),
                    )
                    GyuRunSkeleton(
                        modifier =
                            Modifier
                                .fillMaxWidth(fraction = 0.35f)
                                .height(MaterialTheme.spacing.medium),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun GyuRunListLoadingPreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GyuRunListLoading(itemCount = 3)
        }
    }
}
