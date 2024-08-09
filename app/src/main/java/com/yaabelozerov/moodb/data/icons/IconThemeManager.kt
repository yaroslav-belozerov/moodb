package com.yaabelozerov.moodb.data.icons

import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.model.IconTheme
import com.yaabelozerov.moodb.data.model.ThemeList
import com.yaabelozerov.moodb.di.AppModule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class DualImageResource(val resId: Int? = null, val filePath: String? = null, val rounding: Float = 0f, val tinted: Boolean = false)

class IconThemeManager @Inject constructor(
    @ApplicationContext private val app: Context,
    private val dataStoreManager: AppModule.DataStoreManager,
    val iconManager: IconManager,
    private val moshi: Moshi
) {
    private val _currIconTheme = MutableStateFlow<Map<DefaultMoodType, DualImageResource>?>(null)
    val currIconTheme = _currIconTheme.asStateFlow()

    private val ad = moshi.adapter(ThemeList::class.java).serializeNulls()

    suspend fun fetchTheme(s: String) {
        withContext(Dispatchers.IO) {
            try {
                val theme = IconTheme.valueOf(s)
                Log.i("IconInterceptor", "Loading default theme $s")
                _currIconTheme.update {
                    DefaultMoodType.entries.associateWith {
                        DualImageResource(resId = theme.mapToIconResource(it), tinted = theme.tinted)
                    }
                }.also { Log.i("fetchTheme", _currIconTheme.value.toString()) }
            } catch (e: Exception) {
                Log.i("IconInterceptor", "Default theme $s not found")
                fetchCustomIconThemeOrDefault(s)
            }
        }
    }

    private suspend fun fetchCustomIconThemeOrDefault(themeName: String) {
        dataStoreManager.get(SK.CustomIconThemes).first().let {
            if (it.isBlank()) fetchTheme("SIMPLE")
            else {
                val theme = ad.fromJson(it)!!.list.findLast { it.name == themeName }
                    .also { Log.i("theme", it.toString()) }
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