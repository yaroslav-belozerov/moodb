package com.yaabelozerov.moodb.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.di.MainApplication
import com.yaabelozerov.moodb.presentation.theme.colors.DarkColorScheme
import com.yaabelozerov.moodb.presentation.theme.colors.GreenLightColorScheme
import com.yaabelozerov.moodb.presentation.theme.colors.LightColorScheme
import kotlinx.coroutines.flow.first

enum class ColorSchemes(val key: String, val scheme: ColorScheme?) {
    Light("light", LightColorScheme), Dark("dark", DarkColorScheme), Adapt("adapt", null), Green("green",
        GreenLightColorScheme
    )
}

@Composable
fun MoodbTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    LaunchedEffect(Unit) {
        MainApplication.dataStoreManager.get(SK.Theme).first().let {
            if (it.isBlank()) {
                MainApplication.dataStoreManager.set(
                    SK.Theme,
                    if (darkTheme) ColorSchemes.Dark.key else ColorSchemes.Light.key
                )
            }
        }
    }
    val colorSchemeString by MainApplication.dataStoreManager.get(SK.Theme).collectAsState("")
    val colorScheme = ColorSchemes.entries.find { it.key == colorSchemeString }?.scheme
        ?: (if (darkTheme) DarkColorScheme else LightColorScheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MoodbTypography,
        content = content
    )
}