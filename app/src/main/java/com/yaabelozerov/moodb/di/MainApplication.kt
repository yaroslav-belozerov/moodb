package com.yaabelozerov.moodb.di

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.data.icons.IconManager
import com.yaabelozerov.moodb.data.icons.IconThemeManager
import com.yaabelozerov.moodb.data.model.MoodList
import com.yaabelozerov.moodb.data.model.ThemeList
import com.yaabelozerov.moodb.data.room.mood.MoodDatabase
import com.yaabelozerov.moodb.presentation.locale.LocalizationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class MainApplication : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: MainApplication? = null

        val app by lazy { instance!! }
        val dataStoreManager by lazy { DataStoreManager(instance!!) }
        val localizationManager by lazy { LocalizationManager(instance!!) }
        val moshi: Moshi by lazy { Moshi.Builder().build() }
        val iconManager by lazy { IconManager(instance!!) }
        val iconThemeManager by lazy {
            IconThemeManager(
                iconManager = iconManager,
            )
        }
        val themeListJsonAdapter: JsonAdapter<ThemeList> by lazy {
            moshi.adapter(ThemeList::class.java).serializeNulls()
        }
        val moodListJsonAdapter: JsonAdapter<MoodList> by lazy {
            moshi.adapter(MoodList::class.java).serializeNulls()
        }

        private val recordDatabase by lazy {
            Room.databaseBuilder(
                instance!!,
                MoodDatabase::class.java,
                "record_db"
            ).build()
        }
        val recordDao by lazy { recordDatabase.dao() }
    }
}

private val Context.dataStore by preferencesDataStore("settings")
class DataStoreManager(context: Context) {
    private val settingsDataStore = context.dataStore

    fun <T> get(key: SK<T>): Flow<T> {
        return settingsDataStore.data.map { s ->
            s[key.key] ?: key.default
        }.flowOn(Dispatchers.IO)
    }

    suspend fun <T> set(key: SK<T>, value: T) {
        settingsDataStore.edit { s ->
            s[key.key] = value
        }
    }
}
