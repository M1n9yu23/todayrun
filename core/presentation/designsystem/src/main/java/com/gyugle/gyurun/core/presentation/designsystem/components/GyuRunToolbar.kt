package com.gyugle.gyurun.core.presentation.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.gyugle.gyurun.core.presentation.designsystem.ArrowBackIcon
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GyuRunToolbar(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = ArrowBackIcon,
                        contentDescription = stringResource(R.string.content_description_go_back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@PreviewLightDark
@Composable
private fun GyuRunToolbarPreview() {
    GyuRunTheme {
        GyuRunToolbar(
            title = "오늘도런",
            showBackButton = true,
        )
    }
}