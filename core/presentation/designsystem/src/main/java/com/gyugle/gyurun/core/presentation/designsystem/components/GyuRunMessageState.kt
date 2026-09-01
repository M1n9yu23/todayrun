package com.gyugle.gyurun.core.presentation.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.gyugle.gyurun.core.presentation.designsystem.ErrorIcon
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.R
import com.gyugle.gyurun.core.presentation.designsystem.RunIcon
import com.gyugle.gyurun.core.presentation.designsystem.spacing

@Composable
fun GyuRunEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
) {
    MessageState(
        icon = icon,
        iconTint = MaterialTheme.colorScheme.primary,
        title = title,
        message = message,
        modifier = modifier,
        action =
            actionLabel?.let { label ->
                {
                    GyuRunActionButton(
                        text = label,
                        isLoading = false,
                        onClick = onActionClick,
                    )
                }
            },
    )
}

@Composable
fun GyuRunErrorState(
    message: String,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.state_error_title),
    onRetry: (() -> Unit)? = null,
) {
    MessageState(
        icon = ErrorIcon,
        iconTint = MaterialTheme.colorScheme.error,
        title = title,
        message = message,
        modifier = modifier,
        action =
            onRetry?.let { retry ->
                {
                    GyuRunActionButton(
                        text = stringResource(R.string.state_retry),
                        isLoading = false,
                        onClick = retry,
                    )
                }
            },
    )
}

@Composable
private fun MessageState(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.padding(MaterialTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(
                MaterialTheme.spacing.medium,
                Alignment.CenterVertically,
            ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(MaterialTheme.spacing.huge),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (action != null) {
            action()
        }
    }
}

@PreviewLightDark
@Composable
private fun GyuRunEmptyStatePreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GyuRunEmptyState(
                icon = RunIcon,
                title = "비어있음",
                message = "시작하세요.",
                actionLabel = "Start run",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun GyuRunErrorStatePreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GyuRunErrorState(
                message = "에러발생!!!",
                onRetry = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
