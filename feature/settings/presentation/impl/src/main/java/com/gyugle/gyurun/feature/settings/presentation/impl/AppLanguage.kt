package com.gyugle.gyurun.feature.settings.presentation.impl

import androidx.core.os.LocaleListCompat

internal enum class AppLanguage(
    val localeTag: String?,
) {
    SYSTEM(null),
    ENGLISH("en"),
    KOREAN("ko"),
    ;

    fun toLocaleList(): LocaleListCompat =
        if (localeTag == null) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(localeTag)
        }
}

internal fun appLanguageFromTags(tags: String): AppLanguage =
    AppLanguage.entries.firstOrNull { language ->
        language.localeTag != null && tags.startsWith(language.localeTag)
    } ?: AppLanguage.SYSTEM
