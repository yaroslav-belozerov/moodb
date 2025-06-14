package com.yaabelozerov.moodb.presentation.screens.icontheme

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.yaabelozerov.moodb.R
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.data.model.CustomIconTheme
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.model.ThemeList
import com.yaabelozerov.moodb.data.icons.IconThemeManager
import com.yaabelozerov.moodb.data.model.IconTheme
import com.yaabelozerov.moodb.di.AppModule
import com.yaabelozerov.moodb.di.BaseApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class IconThemeVM @Inject constructor(
    @ApplicationContext private val app: Context,
    private val iconThemeManager: IconThemeManager,
    private val moshi: Moshi
) : ViewModel() {

    private val _iconPicker =
        MutableStateFlow<() -> Unit> { Timber.tag("SettingsViewModel").e("Picker not set!") }
    private val _iconType = MutableStateFlow<Pair<String, DefaultMoodType>?>(null)
    val iconType = _iconType.asStateFlow()

    private val _customThemes = MutableStateFlow(ThemeList(emptyList()))
    val customThemes = _customThemes.asStateFlow()

    private val _currentTheme = MutableStateFlow("")
    val currentTheme = _currentTheme.asStateFlow()

    private val ad = moshi.adapter(ThemeList::class.java)

    fun tryThemeDefault(s: String): IconTheme? {
        return try {
            IconTheme.valueOf(s)
        } catch (_: Exception) {
            null
        }
    }

    fun setIconPicker(function: () -> Unit) {
        _iconPicker.update { function }
    }

    fun setTypeAndSetter(packName: String, type: DefaultMoodType) {
        _iconType.update { Pair(packName, type) }
    }

    fun launchIconPicker() {
        _iconPicker.value.invoke()
    }

    fun addIcon(uri: Uri, callback: suspend (String) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                iconThemeManager.iconManager.addIcon(uri, callback)
            }
        }
    }

    fun fetchCustomThemes() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                BaseApplication.dataStoreManager.get(SK.CustomIconThemes).first().let { got ->
                    val ad = moshi.adapter(ThemeList::class.java)
                    if (got.isBlank()) {
                        BaseApplication.dataStoreManager.set(
                            SK.CustomIconThemes, ad.toJson(
                                ThemeList(emptyList())
                            )
                        )
                        _customThemes.update { ThemeList(emptyList()) }
                    } else {
                        _customThemes.update {
                            ad.fromJson(got)!!
                        }.also { Timber.tag("custom themes got").i(got) }
                    }
                }
                fetchIconsOnce()
            }
        }
    }

    fun createTheme() {
        viewModelScope.launch {
            val theme = app.resources.getString(R.string.theme)
            val name = "$theme ${_customThemes.value.list.size + 1}"
            BaseApplication.dataStoreManager.set(
                SK.CustomIconThemes, ad.toJson(
                    ThemeList(_customThemes.value.list + CustomIconTheme(name, 0f))
                )
            )
            fetchCustomThemes()
        }
    }

    fun setIconPath(pack: String, type: DefaultMoodType, path: String) {
        Timber.tag("setIconPack").i("$pack $type $path")
        viewModelScope.launch {
            BaseApplication.dataStoreManager.set(
                SK.CustomIconThemes, ad.toJson(
                    ThemeList(_customThemes.value.list.map {
                        if (pack == it.name) it.setByType(
                            type, path
                        ) else it
                    })
                )
            )
            fetchCustomThemes()
        }
    }

    fun removeFile(pack: String, type: DefaultMoodType, path: String) {
        Timber.tag("removeFile").i("$pack $type")
        viewModelScope.launch {
            BaseApplication.dataStoreManager.set(
                SK.CustomIconThemes, ad.toJson(
                    ThemeList(_customThemes.value.list.map {
                        if (pack == it.name) it.setByType(type, null) else it
                    })
                )
            )
            iconThemeManager.iconManager.deleteIcon(path)
            fetchCustomThemes()
        }
    }


    fun setTheme(themeName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                BaseApplication.dataStoreManager.set(SK.CurrentIconTheme, themeName)
                fetchIconsOnce()
            }
        }
    }

    fun fetchIconsOnce() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val theme = BaseApplication.dataStoreManager.get(SK.CurrentIconTheme).first()
                _currentTheme.update { theme.ifBlank { "SIMPLE" } }
                iconThemeManager.fetchTheme(theme)
            }
        }
    }

    fun setThemeName(lastName: String, newName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                BaseApplication.dataStoreManager.get(SK.CustomIconThemes).first().let { themes ->
                    if (themes.isBlank()) return@let
                    val ad = moshi.adapter(ThemeList::class.java).serializeNulls()
                    val lst = ad.fromJson(themes)!!.list
                    if (lst.find { it.name == newName } != null) return@let

                    BaseApplication.dataStoreManager.set(
                        SK.CustomIconThemes, ad.toJson(ThemeList(lst.map {
                            if (it.name == lastName) it.copy(name = newName) else it
                        }))
                    )
                    BaseApplication.dataStoreManager.get(SK.CurrentIconTheme).first().let { current ->
                        if (current == lastName) {
                            BaseApplication.dataStoreManager.set(SK.CurrentIconTheme, newName)
                            _currentTheme.update { newName }
                        }
                    }
                }
                fetchCustomThemes()
            }
        }
    }

    fun setRounding(packName: String, rounding: Float) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                BaseApplication.dataStoreManager.get(SK.CustomIconThemes).first().let { themes ->
                    if (themes.isBlank()) return@let
                    val ad = moshi.adapter(ThemeList::class.java).serializeNulls()
                    val lst = ad.fromJson(themes)!!.list

                    BaseApplication.dataStoreManager.set(
                        SK.CustomIconThemes, ad.toJson(ThemeList(lst.map {
                            if (it.name == packName) it.copy(iconRounding = rounding) else it
                        }))
                    )
                }
                fetchCustomThemes()
            }
        }
    }

    fun removeTheme(pack: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                BaseApplication.dataStoreManager.get(SK.CustomIconThemes).first().let { themes ->
                    if (themes.isBlank()) return@let
                    val ad = moshi.adapter(ThemeList::class.java).serializeNulls()
                    val lst = ad.fromJson(themes)!!.list

                    Timber.tag("remove").i("$pack $lst")

                    BaseApplication.dataStoreManager.set(
                        SK.CustomIconThemes, ad.toJson(ThemeList(lst.filter { it.name != pack }))
                    )

                    BaseApplication.dataStoreManager.get(SK.CurrentIconTheme).first().let { current ->
                        if (current == pack) {
                            BaseApplication.dataStoreManager.set(SK.CurrentIconTheme, "SIMPLE")
                            _currentTheme.update { "SIMPLE" }
                        }
                    }
                }
                fetchCustomThemes()
            }
        }
    }
}