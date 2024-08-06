package com.yaabelozerov.moodb.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class ND(
    val route: String,
    val icon: ImageVector? = null,
    val iconEmpty: ImageVector? = null,
    val hasBottomBar: Boolean = true
) { // Nav Destinations
    MainScreen(
        "MainScreen", Icons.Filled.Home, Icons.Outlined.Home
    ),
    SettingsScreen(
        "SettingsScreen", Icons.Filled.Settings, Icons.Outlined.Settings
    ),
    MoodEditAll("MoodEditAll", hasBottomBar = false), MoodEdit(
        "MoodEdit", hasBottomBar = false
    ),
    IconTheme("IconTheme", hasBottomBar = false);

    fun withParam(param: Any) = "${this.route}/$param"
}