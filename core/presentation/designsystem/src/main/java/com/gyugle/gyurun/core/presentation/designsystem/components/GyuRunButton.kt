package com.gyugle.gyurun.core.presentation.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.PlayIcon
import com.gyugle.gyurun.core.presentation.designsystem.spacing

@Composable
fun GyuRunActionButton(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = CircleShape,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(MaterialTheme.spacing.large)
                    .alpha(if (isLoading) 1f else 0f),
                color = LocalContentColor.current
            )
            Row(
                modifier = Modifier.alpha(if (isLoading) 0f else 1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                    )
                }
                Text(text = text)
            }
        }
    }
}

@Composable
fun GyuRunOutlinedActionButton(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = CircleShape,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(MaterialTheme.spacing.large)
                    .alpha(if (isLoading) 1f else 0f),
                color = LocalContentColor.current
            )
            Text(
                text = text,
                modifier = Modifier.alpha(if (isLoading) 0f else 1f)
            )
        }
    }
}

@Composable
fun GyuRunTextButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = CircleShape,
        modifier = modifier
    ) {
        Text(text = text)
    }
}

@PreviewLightDark
@Composable
private fun GyuRunActionButtonPreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                GyuRunActionButton(
                    text = "Start run",
                    isLoading = false,
                    leadingIcon = PlayIcon,
                    onClick = {},
                )
                GyuRunOutlinedActionButton(
                    text = "Cancel",
                    isLoading = false,
                    onClick = {},
                )
            }
        }
    }
}

@Preview
@Composable
private fun GyuRunTextButtonPreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                GyuRunTextButton(
                    text = "Retry",
                    onClick = {},
                )
                GyuRunTextButton(
                    text = "Retry",
                    enabled = false,
                    onClick = {},
                )
            }
        }
    }
}