package com.yaabelozerov.moodb.presentation.common

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cafe.adriel.lyricist.LocalStrings
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.di.MainApplication
import com.yaabelozerov.moodb.presentation.screens.main.MainScreen
import com.yaabelozerov.moodb.presentation.screens.moodedit.MoodEdit
import com.yaabelozerov.moodb.presentation.screens.moodedit.MoodEditAll
import com.yaabelozerov.moodb.presentation.screens.moodedit.MoodEditVM
import com.yaabelozerov.moodb.presentation.screens.settings.SettingsScreen
import com.yaabelozerov.moodb.presentation.screens.settings.SettingsVM
import com.yaabelozerov.moodb.presentation.screens.icontheme.IconTheme
import com.yaabelozerov.moodb.presentation.screens.icontheme.IconThemeVM
import com.yaabelozerov.moodb.presentation.screens.main.MainVM
import com.yaabelozerov.moodb.presentation.theme.ColorSchemes
import com.yaabelozerov.moodb.presentation.theme.extraFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentNavHost(
    modifier: Modifier = Modifier,
    mvm: MainVM,
    svm: SettingsVM,
    itsvm: IconThemeVM,
    mevm: MoodEditVM
) {
    LaunchedEffect(Unit) { itsvm.fetchCustomThemes(); itsvm.fetchIconsOnce() }
    val navController = rememberNavController()
    NavHost(
        modifier = modifier, navController = navController, startDestination = ND.Main
    ) {
        composable<ND.Main>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }) {
            MainScreen(
                navController = navController,
                mvm = mvm,
                ic = svm.iconThemeManager.currIconTheme.collectAsState().value,
            )
        }

        settingsGraph(itsvm, mevm, svm, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.settingsGraph(itsvm: IconThemeVM, mevm: MoodEditVM, svm: SettingsVM, navController: NavController) {
    navigation<ND.Settings>(
        startDestination = ND.SettingsRoot,
        enterTransition = {
            slideInVertically(initialOffsetY = { it })
        },
        exitTransition = {
            ExitTransition.None
        },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { slideOutVertically(targetOffsetY = { it }) }
    ) {
        composable<ND.SettingsRoot> {
            var languageSheetOpen by remember {
                mutableStateOf(false)
            }
            var colorSheetOpen by remember {
                mutableStateOf(false)
            }
            val scope = rememberCoroutineScope()
            LanguageChoiceSheet(languageSheetOpen) { languageSheetOpen = false }
            val currentTheme = itsvm.currentTheme.collectAsState().value
            SettingsScreen(
                routes = listOf(
                    MenuRoute(
                        Icons.Default.Edit, LocalStrings.current.settings.moodTypes, {}
                    ) {
                        navController.navigate(ND.SettingsMoodEditAll)
                        mevm.reloadMoods()
                    }, MenuRoute(
                        Icons.Default.Face,
                        LocalStrings.current.settings.iconTheme,
                        {
                            Text(text = currentTheme, color = MaterialTheme.colorScheme.primary, fontFamily = extraFamily, style = MaterialTheme.typography.titleLarge)
                        }) {
                        navController.navigate(ND.SettingsEditIconTheme)
                    }, MenuRoute(
                        icon =  Icons.Default.InvertColors,
                        name = LocalStrings.current.settings.colorTheme,
                        details = {
                            val currName by MainApplication.dataStoreManager.get(SK.Theme).collectAsState("")
                            val curr = ColorSchemes.entries.find { it.key == currName } ?: ColorSchemes.Light
                            ThemeChoiceSheet(colorSheetOpen) { colorSheetOpen = false }
                            Text(text = LocalStrings.current.colorTheme(curr), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 2.dp))
                        },
                        onClick = { scope.launch {
                            colorSheetOpen = true
                        } }
                    ), MenuRoute(
                        Icons.Default.Language,
                        LocalStrings.current.settings.language,
                        {
                            Text(text = LocalStrings.current.localizedName, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 2.dp))
                        },
                    ) {
                        languageSheetOpen = true
                    }), onBack = { navController.navigateUp() })
        }


        composable<ND.SettingsEditIconTheme>(enterTransition = {
            slideInHorizontally(initialOffsetX = { it })
        }, exitTransition = {
            slideOutHorizontally(targetOffsetX = { it })
        }) {
            IconTheme(
                itsvm = itsvm, onExit = {
                    navController.navigateUp()
                })
        }

        composable<ND.SettingsMoodEditAll>(enterTransition = {
            slideInHorizontally(initialOffsetX = { it })
        }, exitTransition = {
            ExitTransition.None
        }, popEnterTransition = { EnterTransition.None }, popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it })
        }) {
            MoodEditAll(
                types = mevm.currentMoodTypes.collectAsState().value,
                ic = svm.iconThemeManager.currIconTheme.collectAsState().value ?: emptyMap(),
                onBack = { navController.navigateUp() },
                onChoose = { index ->
                    navController.navigate(
                        ND.SettingsMoodEditSingle(index)
                    )
                })
        }

        composable<ND.SettingsMoodEditSingle>(
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }) { backStackEntry ->
            val strings = LocalStrings.current
            val data: ND.SettingsMoodEditSingle = backStackEntry.toRoute()
            val type = mevm.currentMoodTypes.collectAsState().value[data.index]
            MoodEdit(
                type = type,
                onBack = { navController.navigateUp() },
                onSetDefaultType = { type -> mevm.setDefaultType(type, strings.moodType(type.defaultMoodType)) },
                onSetNewType = { type, name, category ->
                    mevm.setNewType(
                        type = type, originalName = strings.moodType(type.defaultMoodType), newName = name, newCategory = category
                    )
                })
        }
    }
}