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
import com.yaabelozerov.moodb.di.BaseApplication
import com.yaabelozerov.moodb.presentation.locale.readLocaleTag
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
    val iconThemeManager: IconThemeManager,
) : ViewModel() {
    private val _locale = MutableStateFlow<String>("")
    val locale = _locale.asStateFlow()

    private val _firstTimeOpen = MutableStateFlow<Boolean?>(null)
    val firstTimeOpen = _firstTimeOpen.asStateFlow()

    init {
//        _firstTimeOpen.update { true }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val cnt = BaseApplication.dataStoreManager.get(SK.TimesVisited).first()
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
                BaseApplication.dataStoreManager.set(SK.TimesVisited, count)
                callback()
            }
        }
        _firstTimeOpen.update { false }
    }
}