package com.yaabelozerov.moodb.di

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.RoomDatabase
import coil.ImageLoader
import coil.imageLoader
import com.squareup.moshi.Moshi
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.data.icons.IconManager
import com.yaabelozerov.moodb.data.icons.IconThemeManager
import com.yaabelozerov.moodb.data.room.mood.MoodDatabase
import com.yaabelozerov.moodb.data.room.mood.RecordDao
import com.yaabelozerov.moodb.data.room.mood.RecordDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val Context.dataStore by preferencesDataStore("settings")

    @Singleton
    @Provides
    fun provideRecordDatabase(@ApplicationContext app: Context): RecordDatabase<RecordDao> = Room.databaseBuilder(
        app,
        MoodDatabase::class.java,
        "record_db"
    ).build() as RecordDatabase<RecordDao>

    @Singleton
    @Provides
    fun provideRecordDao(db: RecordDatabase<RecordDao>) = db.dao()

    @Singleton
    @Provides
    fun provideCoilImageLoader(@ApplicationContext app: Context): ImageLoader = app.imageLoader.newBuilder().crossfade(true).build()

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Singleton
    @Provides
    fun provideIconManager(@ApplicationContext app: Context): IconManager = IconManager(app)

    @Singleton
    @Provides
    fun provideIconInterceptor(
        @ApplicationContext app: Context,
        dataStoreManager: DataStoreManager,
        iconManager: IconManager,
        moshi: Moshi
    ): IconThemeManager = IconThemeManager(app, dataStoreManager, iconManager, moshi)

    @Singleton
    class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {

        private val settingsDataStore = appContext.dataStore

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
}