package com.yaabelozerov.moodb.presentation.locale

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.di.MainApplication
import com.yaabelozerov.moodb.presentation.locale.languages.LocalizationEN
import com.yaabelozerov.moodb.presentation.locale.languages.LocalizationRU
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

data class LocaleNavigation(
    val back: String,
    val next: String,
    val finish: String,
    val close: String,
    val cancel: String,
)

data class LocaleEdit(
    val add: String,
    val remove: String,
    val save: String,
)

data class LocaleSettings(
    val settings: String, val moodTypes: String, val iconTheme: String, val language: String
)

data class LocaleMoodCategory(
    val happy: String,
    val energetic: String,
    val neutral: String,
    val sad: String,
    val angry: String
)

data class Localization(
    val appName: String,
    val localeTag: String,
    val localizedName: String,
    val navigation: LocaleNavigation,
    val welcomeTo: String,
    val chooseLanguage: String,
    val chooseIconTheme: String,
    val today: String,
    val edit: LocaleEdit,
    val settings: LocaleSettings,
    val mood: LocaleMoodCategory,
    val moodType: (DefaultMoodType) -> String,
    val theme: String
)

enum class AvailableLocalizations(val localization: Localization) {
    EN(LocalizationEN), RU(LocalizationRU)
}

fun readLocaleTag(): Flow<String> = MainApplication.dataStoreManager.get(SK.LocaleTag).map {
    it.takeIf { it.isNotBlank() } ?: AppCompatDelegate.getApplicationLocales().get(0)
        ?.toLanguageTag() ?: Locale.getDefault().toLanguageTag()
}

suspend fun setLocaleTag(tag: String) {
    MainApplication.dataStoreManager.set(SK.LocaleTag, tag)
}

class LocalizationManager(context: Context) {
    val localeTag: Flow<String> = readLocaleTag()
}