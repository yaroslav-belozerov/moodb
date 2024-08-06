package com.yaabelozerov.moodb.presentation.screens.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.data.locale.LocaleList
import com.yaabelozerov.moodb.data.icons.IconThemeManager
import com.yaabelozerov.moodb.di.AppModule
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.Locale.LanguageRange
import javax.inject.Inject

@HiltViewModel
class SettingsVM @Inject constructor(
    @ApplicationContext private val app: Context,
    private val dataStoreManager: AppModule.DataStoreManager,
    val iconThemeManager: IconThemeManager,
    val imageLoader: ImageLoader
) : ViewModel() {
    private val _locale = MutableStateFlow<String>("")
    val locale = _locale.asStateFlow()

    private val _firstTimeOpen = MutableStateFlow<Boolean?>(null)
    val firstTimeOpen = _firstTimeOpen.asStateFlow()

    init {
//        _firstTimeOpen.update { true }
        fetchLocale()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val cnt = dataStoreManager.get(SK.TimesVisited).first()
                if (cnt == 0L) {
                    _firstTimeOpen.update { true }
                } else {
                    _firstTimeOpen.update { false }
                    setAppVisits(cnt + 1)
                }
            }
        }
    }

    fun setAppVisits(count: Long, callback: suspend () -> Unit = {}) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataStoreManager.set(SK.TimesVisited, count)
                callback()
            }
        }
        _firstTimeOpen.update { false }
    }

    fun getLocales(): List<Locale> {
        return LocaleList.builtin
    }

    private fun fetchLocale() {
        _locale.update {
            AppCompatDelegate.getApplicationLocales().get(0)?.let {
                it.getDisplayLanguage(it).replaceFirstChar { char -> char.uppercase() }
            } ?: Locale.getDefault().displayName.toString()
        }
    }

    fun setLocale(localeTag: String, callback: suspend () -> Unit = {}) {
        viewModelScope.launch {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeTag))
            callback()
            fetchLocale()
        }
    }
}