package com.gyugle.gyurun.core.presentation.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val PretendardFontFamily =
    FontFamily(
        Font(R.font.pretendard_regular, FontWeight.Normal),
        Font(R.font.pretendard_medium, FontWeight.Medium),
        Font(R.font.pretendard_semibold, FontWeight.SemiBold),
        Font(R.font.pretendard_bold, FontWeight.Bold),
    )

private val baseline = Typography()

val Typography =
    Typography(
        displayLarge = baseline.displayLarge.copy(
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold
        ),
        displayMedium = baseline.displayMedium.copy(
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold
        ),
        displaySmall = baseline.displaySmall.copy(
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold
        ),
        headlineLarge = baseline.headlineLarge.copy(
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold
        ),
        headlineMedium = baseline.headlineMedium.copy(
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold
        ),
        headlineSmall = baseline.headlineSmall.copy(
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold
        ),
        titleLarge = baseline.titleLarge.copy(
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold
        ),
        titleMedium = baseline.titleMedium.copy(
            fontFamily = PretendardFontFamily,
            letterSpacing = 0.sp
        ),
        titleSmall = baseline.titleSmall.copy(
            fontFamily = PretendardFontFamily,
            letterSpacing = 0.sp
        ),
        bodyLarge = baseline.bodyLarge.copy(
            fontFamily = PretendardFontFamily,
            letterSpacing = 0.sp
        ),
        bodyMedium = baseline.bodyMedium.copy(
            fontFamily = PretendardFontFamily,
            letterSpacing = 0.sp
        ),
        bodySmall = baseline.bodySmall.copy(
            fontFamily = PretendardFontFamily,
            letterSpacing = 0.sp
        ),
        labelLarge =
            baseline.labelLarge.copy(
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
            ),
        labelMedium = baseline.labelMedium.copy(
            fontFamily = PretendardFontFamily,
            letterSpacing = 0.sp
        ),
        labelSmall = baseline.labelSmall.copy(
            fontFamily = PretendardFontFamily,
            letterSpacing = 0.sp
        ),
    )

val Typography.statLarge: TextStyle
    get() = displaySmall.copy(fontFeatureSettings = "tnum")

val Typography.statMedium: TextStyle
    get() = headlineSmall.copy(fontFeatureSettings = "tnum")