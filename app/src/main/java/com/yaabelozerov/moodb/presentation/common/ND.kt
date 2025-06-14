package com.yaabelozerov.moodb.presentation.common

import kotlinx.serialization.Serializable

@Serializable
sealed interface ND {
    @Serializable
    data object Main : ND

    @Serializable
    data object Settings : ND

    @Serializable
    data object SettingsRoot: ND

    @Serializable
    data object SettingsMoodEditAll: ND

    @Serializable
    data class SettingsMoodEditSingle(val index: Int): ND

    @Serializable
    data object SettingsEditIconTheme: ND
}