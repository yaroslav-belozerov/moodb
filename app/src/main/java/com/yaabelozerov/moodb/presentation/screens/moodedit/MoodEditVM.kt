package com.yaabelozerov.moodb.presentation.screens.moodedit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.data.model.Category
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.model.MoodList
import com.yaabelozerov.moodb.data.model.MoodType
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
import javax.inject.Inject

@HiltViewModel
class MoodEditVM @Inject constructor(
    @ApplicationContext private val app: Context,
    private val dataStoreManager: AppModule.DataStoreManager,
    private val moshi: Moshi
) : ViewModel() {

    private val _currentMoodTypes = MutableStateFlow(emptyList<MoodType>())
    val currentMoodTypes = _currentMoodTypes.asStateFlow()

    fun reloadMoods() {
        viewModelScope.launch {
            dataStoreManager.get(SK.MoodTypes).first().let { moodTypes ->
                val ad = moshi.adapter(MoodList::class.java).serializeNulls()
                if (moodTypes == SK.MoodTypes.default) {
                    val lst = DefaultMoodType.entries.map {
                        MoodType(it, null, null)
                    }
                    dataStoreManager.set(
                        SK.MoodTypes, ad.toJson(MoodList(list = lst))
                    )
                    _currentMoodTypes.update { lst }
                } else {
                    _currentMoodTypes.update {
                        ad.fromJson(moodTypes)!!.list
                    }
                }
            }
        }
    }

    fun setNewType(
        type: MoodType, newName: String, newCategory: Category
    ) {
        viewModelScope.launch {
            dataStoreManager.set(
                SK.MoodTypes,
                moshi.adapter(MoodList::class.java).serializeNulls()
                    .toJson(MoodList(list = _currentMoodTypes.value.map { curr ->
                        if (type.defaultMoodType == curr.defaultMoodType) {
                            MoodType(
                                curr.defaultMoodType,
                                if (newCategory == type.defaultMoodType.category) null else newCategory,
                                if (newName == app.getString(type.defaultMoodType.nameRes) || newName.isBlank()) null else newName
                            )
                        } else {
                            curr
                        }
                    }))
            )
            reloadMoods()
        }
    }

    fun setDefaultType(
        type: MoodType
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataStoreManager.set(
                    SK.MoodTypes,
                    moshi.adapter(MoodList::class.java).serializeNulls()
                        .toJson(MoodList(list = _currentMoodTypes.value.map { curr ->
                            if (type.defaultMoodType == curr.defaultMoodType) {
                                MoodType(
                                    curr.defaultMoodType,
                                    curr.defaultMoodType.category,
                                    app.getString(curr.defaultMoodType.nameRes)
                                )
                            } else {
                                curr
                            }
                        }))
                )
                reloadMoods()
            }
        }
    }
}