package com.yaabelozerov.moodb.di

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.di.BaseApplication.Companion.instance
import com.yaabelozerov.moodb.presentation.locale.LocalizationManager
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidApp
class BaseApplication : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: BaseApplication? = null

        val dataStoreManager by lazy { DataStoreManager(instance!!) }
        val localizationManager by lazy { LocalizationManager(instance!!) }
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
