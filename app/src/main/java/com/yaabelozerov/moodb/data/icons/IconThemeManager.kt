package com.yaabelozerov.moodb.data.icons

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.model.IconTheme
import com.yaabelozerov.moodb.data.model.ThemeList
import com.yaabelozerov.moodb.di.MainApplication
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

data class DualImageResource(val resId: Int? = null, val filePath: String? = null, val rounding: Float = 0f, val tinted: Boolean = false)

class IconThemeManager(
    val iconManager: IconManager = MainApplication.iconManager,
    private val ad: JsonAdapter<ThemeList> = MainApplication.themeListJsonAdapter,
) {
    private val _currIconTheme = MutableStateFlow<Map<DefaultMoodType, DualImageResource>?>(null)
    val currIconTheme = _currIconTheme.asStateFlow()

    suspend fun fetchTheme(s: String) {
        withContext(Dispatchers.IO) {
            try {
                val theme = IconTheme.valueOf(s)
                Timber.tag("IconInterceptor").i("Loading default theme $s")
                _currIconTheme.update {
                    DefaultMoodType.entries.associateWith {
                        DualImageResource(resId = theme.mapToIconResource(it), tinted = theme.tinted)
                    }
                }.also { Timber.tag("fetchTheme").i(_currIconTheme.value.toString()) }
            } catch (e: Exception) {
                Timber.tag("IconInterceptor").i("Default theme $s not found, $e")
                fetchCustomIconThemeOrDefault(s)
            }
        }
    }

    private suspend fun fetchCustomIconThemeOrDefault(themeName: String) {
        MainApplication.dataStoreManager.get(SK.CustomIconThemes).first().let {
            if (it.isBlank()) fetchTheme("SIMPLE")
            else {
                val theme = ad.fromJson(it)!!.list.findLast { it.name == themeName }
                    .also { Timber.tag("theme").i(it.toString()) }
                if (theme != null) {
                    _currIconTheme.update {
                        DefaultMoodType.entries.associateWith { type ->
                            val path = theme.mapToIconPath(type)
                            if (path != null) {
                                DualImageResource(filePath = path, rounding = theme.iconRounding)
                            } else {
                                DualImageResource(resId = IconTheme.SIMPLE.mapToIconResource(type), tinted = IconTheme.SIMPLE.tinted)
                            }
                        }
                    }
                } else {
                    fetchTheme("SIMPLE")
                }
            }
        }
    }
}