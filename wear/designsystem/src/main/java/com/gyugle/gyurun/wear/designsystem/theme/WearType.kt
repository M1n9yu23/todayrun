package com.gyugle.gyurun.wear.designsystem.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.wear.compose.material3.Typography
import com.gyugle.gyurun.core.presentation.designsystem.R

internal val WearPretendardFontFamily =
    FontFamily(
        Font(R.font.pretendard_regular, FontWeight.Normal),
        Font(R.font.pretendard_medium, FontWeight.Medium),
        Font(R.font.pretendard_semibold, FontWeight.SemiBold),
        Font(R.font.pretendard_bold, FontWeight.Bold),
    )

private fun Typography.withPretendard(): Typography =
    copy(
        arcLarge = arcLarge.copy(fontFamily = WearPretendardFontFamily),
        arcMedium = arcMedium.copy(fontFamily = WearPretendardFontFamily),
        arcSmall = arcSmall.copy(fontFamily = WearPretendardFontFamily),
        displayLarge = displayLarge.copy(fontFamily = WearPretendardFontFamily),
        displayMedium = displayMedium.copy(fontFamily = WearPretendardFontFamily),
        displaySmall = displaySmall.copy(fontFamily = WearPretendardFontFamily),
        titleLarge = titleLarge.copy(fontFamily = WearPretendardFontFamily),
        titleMedium = titleMedium.copy(fontFamily = WearPretendardFontFamily),
        titleSmall = titleSmall.copy(fontFamily = WearPretendardFontFamily),
        labelLarge = labelLarge.copy(fontFamily = WearPretendardFontFamily),
        labelMedium = labelMedium.copy(fontFamily = WearPretendardFontFamily),
        labelSmall = labelSmall.copy(fontFamily = WearPretendardFontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = WearPretendardFontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = WearPretendardFontFamily),
        bodySmall = bodySmall.copy(fontFamily = WearPretendardFontFamily),
        bodyExtraSmall = bodyExtraSmall.copy(fontFamily = WearPretendardFontFamily),
        numeralExtraLarge = numeralExtraLarge.copy(fontFamily = WearPretendardFontFamily),
        numeralLarge = numeralLarge.copy(fontFamily = WearPretendardFontFamily),
        numeralMedium = numeralMedium.copy(fontFamily = WearPretendardFontFamily),
        numeralSmall = numeralSmall.copy(fontFamily = WearPretendardFontFamily),
        numeralExtraSmall = numeralExtraSmall.copy(fontFamily = WearPretendardFontFamily),
    )

val WearTypography = Typography().withPretendard()
