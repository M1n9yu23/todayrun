package com.gyugle.gyurun.core.presentation.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.gyugle.gyurun.core.presentation.designsystem.ErrorIcon
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme

@Composable
fun GyuRunDialog(
    title: String,
    description: String,
    onDismiss: () -> Unit,
    primaryButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    secondaryButton: (@Composable () -> Unit)? = null,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        icon =
            if (icon != null) {
                { Icon(imageVector = icon, contentDescription = null) }
            } else {
                null
            },
        title = { Text(text = title) },
        text = { Text(text = description) },
        confirmButton = primaryButton,
        dismissButton = secondaryButton,
    )
}

@PreviewLightDark
@Composable
private fun GyuRunDialogPreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GyuRunDialog(
                title = "진행 중이던 러닝이 있어요",
                description = "저장되지 않은 러닝을 이어서 기록할까요, 아니면 폐기할까요?",
                icon = ErrorIcon,
                onDismiss = {},
                primaryButton = {
                    TextButton(onClick = {}) {
                        Text(text = "이어서 기록")
                    }
                },
                secondaryButton = {
                    TextButton(onClick = {}) {
                        Text(
                            text = "폐기",
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )
        }
    }
}
