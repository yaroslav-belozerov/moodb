package com.yaabelozerov.moodb.presentation.common

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.rememberStrings
import com.yaabelozerov.moodb.BuildConfig
import com.yaabelozerov.moodb.di.MainApplication
import com.yaabelozerov.moodb.presentation.screens.settings.SettingsVM
import com.yaabelozerov.moodb.onboarding.FirstTimeScreen
import com.yaabelozerov.moodb.presentation.locale.AvailableLocalizations
import com.yaabelozerov.moodb.presentation.screens.moodedit.MoodEditVM
import com.yaabelozerov.moodb.presentation.screens.icontheme.IconThemeVM
import com.yaabelozerov.moodb.presentation.screens.main.MainVM
import com.yaabelozerov.moodb.presentation.theme.MoodbTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val mvm by viewModels<MainVM>()
    private val svm by viewModels<SettingsVM>()
    private val itsvm by viewModels<IconThemeVM>()
    private val mevm by viewModels<MoodEditVM>()

    private val pickPng =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                itsvm.addIcon(uri) { name ->
                    itsvm.iconType.value?.let { type ->
                        itsvm.setIconPath(type.first, type.second, name)
                    }
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        itsvm.setIconPicker {
            pickPng.launch(
                PickVisualMediaRequest.Builder().setMediaType(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                ).build()
            )
        }


        setContent {
            val localeTag by MainApplication.localizationManager.localeTag.collectAsState("")
            val lyricist = rememberStrings(
                translations = AvailableLocalizations.entries.associate { it.localization.localeTag to it.localization },
                currentLanguageTag = localeTag
            )
            LaunchedEffect(localeTag) {
                if (localeTag.isNotBlank()) {
                    mvm.groupRecordsByMonthDay()
                }
            }

            ProvideStrings(
                lyricist = lyricist,
            ) {
                MoodbTheme {
                    val isFirstTime = svm.firstTimeOpen.collectAsState().value
                    Crossfade(targetState = isFirstTime) { firstTime ->
                        if (firstTime == true) {
                            FirstTimeScreen(svm = svm, itsvm = itsvm)
                        } else if (firstTime == false) {
                            ContentNavHost(
                                mvm = mvm,
                                svm = svm,
                                itsvm = itsvm,
                                mevm = mevm,
                            )
                        }
                    }
                }
            }
        }
    }
}