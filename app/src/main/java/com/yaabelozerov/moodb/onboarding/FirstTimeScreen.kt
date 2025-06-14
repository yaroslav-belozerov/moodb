package com.yaabelozerov.moodb.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.LocalStrings
import com.yaabelozerov.moodb.data.icons.DualImageResource
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.presentation.common.DualAsyncImage
import com.yaabelozerov.moodb.presentation.locale.AvailableLocalizations
import com.yaabelozerov.moodb.presentation.locale.setLocaleTag
import com.yaabelozerov.moodb.presentation.screens.settings.SettingsVM
import com.yaabelozerov.moodb.presentation.screens.icontheme.IconThemeVM
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun FirstTimeScreen(
    modifier: Modifier = Modifier,
    svm: SettingsVM,
    itsvm: IconThemeVM
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
    Scaffold { innerPadding ->
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                , state = pager
        ) { page ->
            when (page) {
                0 -> {
                    WelcomeLanguage(
                        modifier = modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        onSetLocale = { tag ->
                            scope.launch {
                                setLocaleTag(tag)
                            }
                        },
                        onNext = {
                            scope.launch {
                                pager.animateScrollToPage(pager.currentPage + 1)
                            }
                        })
                }

                1 -> {
                    OnboardingThemeChooser(
                        itsvm = itsvm,
                        onNext = {
                            svm.setAppVisits(1)
                        },
                        onBack = {  scope.launch {
                            pager.animateScrollToPage(pager.currentPage - 1)
                        } },
                        modifier = modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WelcomeLanguage(
    modifier: Modifier = Modifier,
    onSetLocale: (String) -> Unit,
    onNext: () -> Unit,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(verticalArrangement = Arrangement.spacedBy((16).dp)) {
            Column(verticalArrangement = Arrangement.spacedBy((-16).dp)) {
                Text(
                    LocalStrings.current.welcomeTo,
                    fontSize = 30.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = LocalStrings.current.appName,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    fontSize = 80.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val sheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()
            TextButton(
                onClick = { scope.launch { sheetState.show() } },
                shape = MaterialTheme.shapes.extraSmall
            ) {
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
                                LocalStrings.current.chooseLanguage,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        AvailableLocalizations.entries.map {
                            TextButton(
                                modifier = Modifier.fillMaxWidth(), onClick = {
                                    scope.launch {
                                        sheetState.hide()
                                        onSetLocale(it.localization.localeTag)
                                    }
                                }, shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Text(
                                    text = it.localization.localizedName,
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
                Text(
                    text = Locale.getDefault().displayLanguage.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = 8.dp).padding(top = 4.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null
                )
            }
            Button(onClick = onNext, shape = MaterialTheme.shapes.extraSmall) {
                Text(
                    LocalStrings.current.navigation.next,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 8.dp).padding(top = 4.dp)
                )
                Icon(Icons.AutoMirrored.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun OnboardingThemeChooser(itsvm: IconThemeVM, onNext: () -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) { itsvm.fetchCustomThemes() }
    val chosen by itsvm.currentTheme.collectAsState()
    val themes by itsvm.customThemes.collectAsState()
    val scope = rememberCoroutineScope()
    Column(modifier = modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.SpaceBetween) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(LocalStrings.current.chooseIconTheme, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
            FlowRow (
                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(0.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                com.yaabelozerov.moodb.data.model.IconTheme.entries.forEach {
                    var currentIndex by remember { mutableIntStateOf(0) }
                    LaunchedEffect(Unit) {
                        scope.launch {
                            while (true) {
                                currentIndex =
                                    if (currentIndex == DefaultMoodType.entries.size - 1) 0 else currentIndex + 1; delay(
                                    1000
                                )
                            }
                        }
                    }
                    OutlinedCard(shape = MaterialTheme.shapes.extraSmall, onClick = { itsvm.setTheme(it.name) }, colors = CardDefaults.outlinedCardColors().copy(containerColor = if (it.name == chosen) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent)) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(it.name, style = MaterialTheme.typography.titleLarge)
                            Crossfade(currentIndex) { index ->
                                DualAsyncImage(
                                    imageModifier = Modifier.size(64.dp), dualIconResource = DualImageResource(
                                        resId = it.mapToIconResource(DefaultMoodType.entries[index]),
                                        tinted = it.tinted
                                    )
                                )
                            }
                        }
                    }
                }
                themes.list.forEach { theme ->
                    OutlinedCard(shape = MaterialTheme.shapes.extraSmall, onClick = { itsvm.setTheme(theme.name) }, colors = CardDefaults.outlinedCardColors().copy(containerColor = if (theme.name == chosen) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent)) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(theme.name, style = MaterialTheme.typography.titleLarge)

                            var currentIndex by remember { mutableIntStateOf(0) }
                            LaunchedEffect(Unit) {
                                scope.launch {
                                    while (true) {
                                        currentIndex =
                                            if (currentIndex == DefaultMoodType.entries.size - 1) 0 else currentIndex + 1; delay(
                                            1000
                                        )
                                    }
                                }
                            }
                            Crossfade(currentIndex) { index ->
                                val iconPath = theme.mapToIconPath(DefaultMoodType.entries[index])
                                DualAsyncImage(
                                    imageModifier = Modifier.size(72.dp), dualIconResource = DualImageResource(
                                        com.yaabelozerov.moodb.data.model.IconTheme.SIMPLE.mapToIconResource(
                                            DefaultMoodType.entries[index]
                                        ),
                                        iconPath,
                                        theme.iconRounding,
                                        com.yaabelozerov.moodb.data.model.IconTheme.SIMPLE.tinted
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBack, shape = MaterialTheme.shapes.extraSmall) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                Text(
                    LocalStrings.current.navigation.back,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 8.dp).padding(top = 4.dp)
                )
            }
            Button(onClick = onNext, shape = MaterialTheme.shapes.extraSmall) {
                Text(
                    LocalStrings.current.navigation.finish,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 8.dp).padding(top = 4.dp)
                )
                Icon(Icons.AutoMirrored.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}