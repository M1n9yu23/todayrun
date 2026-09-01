package com.gyugle.gyurun.core.presentation.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.gyugle.gyurun.core.presentation.designsystem.ArrowBackIcon
import com.gyugle.gyurun.core.presentation.designsystem.ArrowUpIcon
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.SettingsIcon
import com.gyugle.gyurun.core.presentation.designsystem.spacing

@Composable
fun GyuRunIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(MaterialTheme.spacing.huge),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}

@Composable
fun GyuRunFilledIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilledTonalIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(MaterialTheme.spacing.huge),
        colors =
            IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
        )
    }
}

@PreviewLightDark
@Composable
private fun GyuRunIconButtonPreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                GyuRunIconButton(
                    icon = SettingsIcon,
                    contentDescription = "Open Settings",
                    onClick = {},
                )
                GyuRunIconButton(
                    icon = ArrowUpIcon,
                    contentDescription = "Open in browser",
                    onClick = {},
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun GyuRunFilledIconButtonPreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                GyuRunFilledIconButton(
                    icon = ArrowBackIcon,
                    contentDescription = "Exit",
                    onClick = {},
                )
                GyuRunFilledIconButton(
                    icon = ArrowBackIcon,
                    contentDescription = "Exit",
                    onClick = {},
                    enabled = false,
                )
            }
        }
    }
}
