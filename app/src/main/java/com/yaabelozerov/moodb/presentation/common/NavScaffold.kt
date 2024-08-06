package com.yaabelozerov.moodb.presentation.common

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yaabelozerov.moodb.R
import com.yaabelozerov.moodb.data.model.IconTheme
import com.yaabelozerov.moodb.presentation.screens.main.MainScreen
import com.yaabelozerov.moodb.presentation.screens.moodedit.MoodEdit
import com.yaabelozerov.moodb.presentation.screens.moodedit.MoodEditAll
import com.yaabelozerov.moodb.presentation.screens.moodedit.MoodEditVM
import com.yaabelozerov.moodb.presentation.screens.settings.SettingsScreen
import com.yaabelozerov.moodb.presentation.screens.settings.SettingsVM
import com.yaabelozerov.moodb.presentation.screens.icontheme.IconTheme
import com.yaabelozerov.moodb.presentation.screens.icontheme.IconThemeVM
import com.yaabelozerov.moodb.presentation.screens.main.MainVM

@Composable
fun ContentNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mvm: MainVM,
    svm: SettingsVM,
    itsvm: IconThemeVM,
    mevm: MoodEditVM
) {
    NavHost(
        modifier = modifier, navController = navController, startDestination = ND.MainScreen.route
    ) {
        composable(ND.MainScreen.route) {
            MainScreen(
                navController = navController,
                mvm = mvm,
                ic = svm.iconThemeManager.currIconTheme.collectAsState().value ?: emptyMap(),
                imageLoader = svm.imageLoader
            )
        }

        composable(ND.SettingsScreen.route) {
            var selecting by remember {
                mutableStateOf(false)
            }
            if (selecting) Dialog(onDismissRequest = { selecting = false }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    svm.getLocales().map {
                        Card(onClick = {
                            selecting = false
                            svm.setLocale(it.toLanguageTag()) {
                                mvm.fetchMonths()
                            }
                        }) {
                            Text(modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                                text = it.displayName.replaceFirstChar { char -> char.uppercase() })
                        }
                    }
                }
            }
            val currentTheme = itsvm.currentTheme.collectAsState().value
            SettingsScreen(routes = listOf(MenuRoute(
                Icons.Default.Edit, stringResource(id = R.string.edit_mood_types), ""
            ) {
                navController.navigate(ND.MoodEditAll.route)
                mevm.reloadMoods()
            }, MenuRoute(
                Icons.Default.Face,
                stringResource(id = R.string.icon_theme),
                itsvm.tryThemeDefault(currentTheme)?.let { stringResource(id = it.nameRes) } ?: currentTheme
            ) {
                navController.navigate(ND.IconTheme.route)
            }, MenuRoute(
                Icons.Default.LocationOn, stringResource(id = R.string.language), svm.locale.collectAsState().value,
            ) {
                selecting = true
            }), onBack = { navController.navigateUp() })
        }

        composable(ND.IconTheme.route, enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start)
        }, exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.End)
        }) {
            IconTheme(imageLoader = svm.imageLoader,
                chosen = itsvm.currentTheme.collectAsState().value,
                themes = itsvm.customThemes.collectAsState().value,
                onBack = { navController.navigateUp() },
                onCreateTheme = { itsvm.createTheme() },
                onChooseIcon = { packName, type ->
                    itsvm.setTypeAndSetter(packName, type)
                    itsvm.launchIconPicker()
                },
                onChangeRounding = { packName, rounding ->
                    itsvm.setRounding(packName, rounding)
                },
                onSavePackName = { old, new ->
                    itsvm.setThemeName(old, new)
                },
                onSetCurrentTheme = { new ->
                    itsvm.setTheme(new)
                },
                onRemoveIcon = { pack, type, path ->
                    itsvm.removeFile(pack, type, path)
                },
                onRemoveTheme = { pack ->
                    itsvm.removeTheme(pack)
                })
        }

        composable(ND.MoodEditAll.route, enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start)
        }, exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.End)
        }) {
            MoodEditAll(imageLoader = svm.imageLoader,
                types = mevm.currentMoodTypes.collectAsState().value,
                ic = svm.iconThemeManager.currIconTheme.collectAsState().value ?: emptyMap(),
                onBack = { navController.navigateUp() },
                onChoose = { index ->
                    navController.navigate(
                        ND.MoodEdit.withParam(index)
                    )
                })
        }

        composable(ND.MoodEdit.withParam("{typeIndex}"),
            arguments = listOf(navArgument("typeIndex") {
                type = NavType.IntType
            }),
            enterTransition = {
                slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start)
            },
            exitTransition = {
                slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.End)
            }) { backStackEntry ->
            val type =
                mevm.currentMoodTypes.collectAsState().value[backStackEntry.arguments!!.getInt("typeIndex")]
            MoodEdit(type = type,
                onBack = { navController.navigateUp() },
                onSetDefaultType = { type -> mevm.setDefaultType(type) },
                onSetNewType = { type, name, category -> mevm.setNewType(type, name, category) })
        }
    }
}