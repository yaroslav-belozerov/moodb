package com.yaabelozerov.moodb.presentation.common

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.yaabelozerov.moodb.R
import com.yaabelozerov.moodb.presentation.screens.main.MainScreen
import com.yaabelozerov.moodb.presentation.screens.moodedit.MoodEdit
import com.yaabelozerov.moodb.presentation.screens.moodedit.MoodEditAll
import com.yaabelozerov.moodb.presentation.screens.moodedit.MoodEditVM
import com.yaabelozerov.moodb.presentation.screens.settings.SettingsScreen
import com.yaabelozerov.moodb.presentation.screens.settings.SettingsVM
import com.yaabelozerov.moodb.presentation.screens.icontheme.IconTheme
import com.yaabelozerov.moodb.presentation.screens.icontheme.IconThemeVM
import com.yaabelozerov.moodb.presentation.screens.main.MainVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mvm: MainVM,
    svm: SettingsVM,
    itsvm: IconThemeVM,
    mevm: MoodEditVM
) {
    LaunchedEffect(Unit) { itsvm.fetchCustomThemes(); itsvm.fetchIconsOnce() }
    NavHost(
        modifier = modifier, navController = navController, startDestination = ND.MainScreen.route
    ) {
        composable(ND.MainScreen.route, enterTransition = { EnterTransition.None }, exitTransition = { ExitTransition.None }) {
            MainScreen(
                navController = navController,
                mvm = mvm,
                ic = svm.iconThemeManager.currIconTheme.collectAsState().value,
            )
        }

        navigation(
            startDestination = "SETTINGS",
            route = ND.SettingsScreen.route,
            enterTransition = {
                slideInVertically(initialOffsetY = { it })
            },
            exitTransition = {
                ExitTransition.None
            }, popEnterTransition = { EnterTransition.None }, popExitTransition = { slideOutVertically(targetOffsetY = {it}) }) {

            composable("SETTINGS") {
                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()
                if (sheetState.isVisible) ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { scope.launch { sheetState.hide() } },
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Text(
                                "Choose a language",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val locales by remember { mutableStateOf(svm.getLocales()) }
                        locales.map {
                            TextButton(
                                modifier = Modifier.fillMaxWidth(), onClick = {
                                    scope.launch {
                                        sheetState.hide()
                                        svm.setLocale(it.toLanguageTag())
                                    }
                                }, shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Text(
                                    text = it.displayName.replaceFirstChar { char -> char.uppercase() },
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }

                val currentTheme = itsvm.currentTheme.collectAsState().value
                SettingsScreen(
                    routes = listOf(
                        MenuRoute(
                    Icons.Default.Edit, stringResource(id = R.string.edit_mood_types), ""
                ) {
                    navController.navigate(ND.MoodEditAll.route)
                    mevm.reloadMoods()
                }, MenuRoute(
                    Icons.Default.Face,
                    stringResource(id = R.string.icon_theme),
                    itsvm.tryThemeDefault(currentTheme)?.let { stringResource(id = it.nameRes) }
                        ?: currentTheme) {
                    navController.navigate(ND.IconTheme.route)
                }, MenuRoute(
                    Icons.Default.LocationOn,
                    stringResource(id = R.string.language),
                    svm.locale.collectAsState().value,
                ) {
                    scope.launch { sheetState.show() }
                }), onBack = { navController.navigateUp() })
            }


            composable(ND.IconTheme.route, enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            }, exitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }) {
                IconTheme(
                    itsvm = itsvm, onExit = {
                        navController.navigateUp()
                    })
            }

            composable(ND.MoodEditAll.route, enterTransition = {
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
                            ND.MoodEdit.withParam(index)
                        )
                    })
            }

            composable(
                ND.MoodEdit.withParam("{typeIndex}"),
                arguments = listOf(navArgument("typeIndex") {
                    type = NavType.IntType
                }),
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it })
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { it })
                }) { backStackEntry ->
                val type =
                    mevm.currentMoodTypes.collectAsState().value[backStackEntry.arguments!!.getInt("typeIndex")]
                MoodEdit(
                    type = type,
                    onBack = { navController.navigateUp() },
                    onSetDefaultType = { type -> mevm.setDefaultType(type) },
                    onSetNewType = { type, name, category ->
                        mevm.setNewType(
                            type, name, category
                        )
                    })
            }
        }
    }
}