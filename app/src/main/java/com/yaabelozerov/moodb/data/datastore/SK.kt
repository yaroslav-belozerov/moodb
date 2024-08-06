package com.yaabelozerov.moodb.data.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

sealed class SK<T>(val key: Preferences.Key<T>, val default: T) { // Settings Keys
    data object MoodTypes : SK<String>(stringPreferencesKey("mood_types"), "")
    data object TimesVisited : SK<Long>(longPreferencesKey("first_time_visit"), 0)
    data object CustomIconThemes : SK<String>(stringPreferencesKey("custom_icon_themes"), "")
    data object CurrentIconTheme : SK<String>(stringPreferencesKey("current_icon_theme"), "")
}