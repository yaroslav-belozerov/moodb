package com.yaabelozerov.moodb.presentation.common

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.rememberNavController
import com.yaabelozerov.moodb.presentation.screens.settings.SettingsVM
import com.yaabelozerov.moodb.onboarding.FirstTimeScreen
import com.yaabelozerov.moodb.presentation.screens.moodedit.MoodEditVM
import com.yaabelozerov.moodb.presentation.screens.icontheme.IconThemeVM
import com.yaabelozerov.moodb.presentation.screens.main.MainVM
import com.yaabelozerov.moodb.presentation.theme.MoodbTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

        itsvm.setIconPicker {
            pickPng.launch(
                PickVisualMediaRequest.Builder().setMediaType(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                ).build()
            )
        }

        setContent {
            val navController = rememberNavController()

            MoodbTheme {
                val isFirstTime = svm.firstTimeOpen.collectAsState().value
                Crossfade(targetState = isFirstTime) { firstTime ->
                    if (firstTime == true) {
                        FirstTimeScreen(svm = svm, itsvm = itsvm, mevm = mevm)
                    } else if (firstTime == false) {
                        ContentNavHost(
                            navController = navController,
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