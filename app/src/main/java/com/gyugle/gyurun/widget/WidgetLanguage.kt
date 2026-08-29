package com.gyugle.gyurun.widget

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WidgetLanguage {
    private val tags = MutableStateFlow("")

    val languageTags: StateFlow<String> = tags.asStateFlow()

    fun set(languageTags: String) {
        tags.value = languageTags
    }
}