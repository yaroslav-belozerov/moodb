package com.yaabelozerov.moodb.presentation.theme.colors

import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

private val primaryLight = Color(0xFF3C6400)
private val onPrimaryLight = Color(0xFFFFFFFF)
private val primaryContainerLight = Color(0xFF4D7F00)
private val onPrimaryContainerLight = Color(0xFFECFFD0)
private val secondaryLight = Color(0xFF4F6535)
private val onSecondaryLight = Color(0xFFFFFFFF)
private val secondaryContainerLight = Color(0xFFCEE9AC)
private val onSecondaryContainerLight = Color(0xFF536939)
private val tertiaryLight = Color(0xFF00664D)
private val onTertiaryLight = Color(0xFFFFFFFF)
private val tertiaryContainerLight = Color(0xFF008263)
private val onTertiaryContainerLight = Color(0xFFE4FFF1)
private val errorLight = Color(0xFFBA1A1A)
private val onErrorLight = Color(0xFFFFFFFF)
private val errorContainerLight = Color(0xFFFFDAD6)
private val onErrorContainerLight = Color(0xFF93000A)
private val backgroundLight = Color(0xFFF8FBEC)
private val onBackgroundLight = Color(0xFF191D14)
private val surfaceLight = Color(0xFFF8FBEC)
private val onSurfaceLight = Color(0xFF191D14)
private val surfaceVariantLight = Color(0xFFDFE5CF)
private val onSurfaceVariantLight = Color(0xFF424939)
private val outlineLight = Color(0xFF737A67)
private val outlineVariantLight = Color(0xFFC2C9B4)
private val scrimLight = Color(0xFF000000)
private val inverseSurfaceLight = Color(0xFF2E3228)
private val inverseOnSurfaceLight = Color(0xFFEFF2E4)
private val inversePrimaryLight = Color(0xFF9ED75A)
private val surfaceDimLight = Color(0xFFD9DBCE)
private val surfaceBrightLight = Color(0xFFF8FBEC)
private val surfaceContainerLowestLight = Color(0xFFFFFFFF)
private val surfaceContainerLowLight = Color(0xFFF2F5E7)
private val surfaceContainerLight = Color(0xFFEDEFE1)
private val surfaceContainerHighLight = Color(0xFFE7EADB)
private val surfaceContainerHighestLight = Color(0xFFE1E4D6)

val GreenLightColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)
