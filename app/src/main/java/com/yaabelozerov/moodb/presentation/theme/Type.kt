package com.yaabelozerov.moodb.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yaabelozerov.moodb.R

private val baseline = Typography()
private val displayFamily = FontFamily(
    Font(R.font.cormorant_infant, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.cormorant_infant_italic, FontWeight.Normal, FontStyle.Italic),
)
private val bodyFamily = FontFamily(
    Font(R.font.overpass_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.overpass_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.overpass_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.overpass_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.overpass_black, FontWeight.Black, FontStyle.Normal),
    Font(R.font.overpass_blackitalic, FontWeight.Black, FontStyle.Italic),
    Font(R.font.overpass_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.overpass_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.overpass_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.overpass_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.overpass_thin, FontWeight.Thin, FontStyle.Normal),
    Font(R.font.overpass_thinitalic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.overpass_semibold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.overpass_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.overpass_extralight, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.overpass_extralightitalic, FontWeight.SemiBold, FontStyle.Italic),
)


@OptIn(ExperimentalTextApi::class)
val extraFamily = FontFamily(
    Font(R.font.doto, variationSettings = FontVariation.Settings(
        FontVariation.weight(900)
    ))
)

val MoodbTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = bodyFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = bodyFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = bodyFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFamily),
)