package com.gyugle.gyurun.core.presentation.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunElevation
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.spacing

@Composable
fun GyuRunCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = GyuRunElevation.card,
    ) {
        Box(modifier = Modifier.padding(MaterialTheme.spacing.large)) {
            content()
        }
    }
}

@PreviewLightDark
@Composable
private fun GyuRunCardPreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                GyuRunCard {
                    Text(
                        text = "Ready to run",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
            }
        }
    }
}