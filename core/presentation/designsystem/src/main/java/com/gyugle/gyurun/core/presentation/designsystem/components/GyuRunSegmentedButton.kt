package com.gyugle.gyurun.core.presentation.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.designsystem.spacing

@Composable
fun <T> GyuRunSegmentedButton(
    options: List<T>,
    selectedOption: T,
    onOptionSelect: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == selectedOption,
                onClick = { onOptionSelect(option) },
                shape =
                    SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size,
                    ),
            ) {
                Text(
                    text = optionLabel(option),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun GyuRunSegmentedButtonPreview() {
    GyuRunTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            var selected by remember { mutableStateOf("킬로미터") }
            GyuRunSegmentedButton(
                options = listOf("킬로미터", "마일"),
                selectedOption = selected,
                onOptionSelect = { selected = it },
                optionLabel = { it },
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
            )
        }
    }
}