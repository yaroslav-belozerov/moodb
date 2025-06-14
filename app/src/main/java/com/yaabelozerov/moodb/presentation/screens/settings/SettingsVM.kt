package com.yaabelozerov.moodb.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.data.icons.IconThemeManager
import com.yaabelozerov.moodb.di.MainApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsVM(
    val iconThemeManager: IconThemeManager = MainApplication.iconThemeManager,
) : ViewModel() {
    private val _locale = MutableStateFlow<String>("")
    val locale = _locale.asStateFlow()

    private val _firstTimeOpen = MutableStateFlow<Boolean?>(null)
    val firstTimeOpen = _firstTimeOpen.asStateFlow()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val cnt = MainApplication.dataStoreManager.get(SK.TimesVisited).first()
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
                MainApplication.dataStoreManager.set(SK.TimesVisited, count)
                callback()
            }
        }
        _firstTimeOpen.update { false }
    }
}