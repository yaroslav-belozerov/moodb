package com.yaabelozerov.moodb.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yaabelozerov.moodb.R
import com.yaabelozerov.moodb.presentation.screens.icontheme.IconTheme
import com.yaabelozerov.moodb.presentation.screens.moodedit.MoodEditVM
import com.yaabelozerov.moodb.presentation.screens.settings.SettingsVM
import com.yaabelozerov.moodb.presentation.screens.icontheme.IconThemeVM
import kotlinx.coroutines.launch

@Composable
fun FirstTimeScreen(
    modifier: Modifier = Modifier, svm: SettingsVM, itsvm: IconThemeVM, mevm: MoodEditVM
) {
    val scope = rememberCoroutineScope()
    val pager = rememberPagerState(initialPage = 0, pageCount = { Destinations.entries.size })
    BackHandler {
        if (pager.currentPage != 0) {
            scope.launch {
                pager.animateScrollToPage(pager.currentPage - 1)
            }
        }
    }
    Scaffold(floatingActionButton = {
        Row {
            if (pager.currentPage != 0) {
                FloatingActionButton(onClick = {
                    scope.launch {
                        pager.animateScrollToPage(pager.currentPage - 1)
                    }
                }) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = null
                        )
                        Text(
                            modifier = Modifier.padding(8.dp, 0.dp),
                            text = stringResource(id = R.string.back),
                            fontSize = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            FloatingActionButton(onClick = {
                if (pager.currentPage == pager.pageCount - 1) {
                    mevm.reloadMoods()
                    svm.setAppVisits(1)
                } else {
                    scope.launch {
                        pager.animateScrollToPage(pager.currentPage + 1)
                    }
                }
            }) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (pager.currentPage == pager.pageCount - 1) {
                        Text(
                            modifier = Modifier.padding(8.dp, 0.dp),
                            text = stringResource(id = R.string.complete),
                            fontSize = 20.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Check, contentDescription = null
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(8.dp, 0.dp),
                            text = stringResource(id = R.string.next),
                            fontSize = 20.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }) { innerPadding ->
        HorizontalPager(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(), state = pager) { page ->
            when (page) {
                0 -> {
                    WelcomeLanguage(modifier = modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                        svm.getLocales(),
                        onSetLocale = { tag ->
                            svm.setLocale(tag)
                        })
                }

                1 -> {
                    IconTheme(imageLoader = svm.imageLoader,
                        chosen = itsvm.currentTheme.collectAsState().value,
                        themes = itsvm.customThemes.collectAsState().value,
                        onBack = null,
                        onCreateTheme = {
                            itsvm.createTheme()
                        },
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
            }
        }
    }
}