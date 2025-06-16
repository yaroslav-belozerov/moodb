package com.yaabelozerov.moodb.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.LocalStrings
import com.yaabelozerov.moodb.data.datastore.SK
import com.yaabelozerov.moodb.data.icons.DualImageResource
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.model.IconTheme
import com.yaabelozerov.moodb.di.MainApplication
import com.yaabelozerov.moodb.presentation.common.DualAsyncImage
import com.yaabelozerov.moodb.presentation.common.LanguageChoiceSheet
import com.yaabelozerov.moodb.presentation.locale.AvailableLocalizations
import com.yaabelozerov.moodb.presentation.locale.setLocaleTag
import com.yaabelozerov.moodb.presentation.screens.settings.SettingsVM
import com.yaabelozerov.moodb.presentation.screens.icontheme.IconThemeVM
import com.yaabelozerov.moodb.presentation.theme.ColorSchemes
import com.yaabelozerov.moodb.presentation.theme.extraFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@Composable
fun FirstTimeScreen(
    modifier: Modifier = Modifier, svm: SettingsVM, itsvm: IconThemeVM
) {
    val scope = rememberCoroutineScope()
    val pager = rememberPagerState(initialPage = 0, pageCount = { 2 })
    BackHandler {
        if (pager.currentPage != 0) {
            scope.launch {
                pager.animateScrollToPage(pager.currentPage - 1)
            }
        }
    }
    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(8.dp)
                    .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(pager.pageCount) { index ->
                    val color by animateColorAsState(
                        if (pager.currentPage == 0) {
                            MaterialTheme.colorScheme.onBackground.copy(0f)
                        } else if (index == pager.currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                        }
                    )
                    Spacer(
                        Modifier
                            .clip(MaterialTheme.shapes.extraLarge)
                            .height(8.dp)
                            .weight(1f)
                            .background(color)
                    )
                }
            }
            HorizontalPager(
                modifier = Modifier.weight(1f), state = pager
            ) { page ->
                when (page) {
                    0 -> {
                        WelcomeLanguage(
                            modifier = modifier.fillMaxSize(), onNext = {
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
                            onBack = {
                                scope.launch {
                                    pager.animateScrollToPage(pager.currentPage - 1)
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WelcomeLanguage(
    modifier: Modifier = Modifier,
    onNext: () -> Unit,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            val infiniteTransition = rememberInfiniteTransition()
            val rot by infiniteTransition.animateFloat(
                initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 50000, easing = LinearEasing
                    )
                )
            )
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f, targetValue = 1.1f, animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 4000, easing = EaseInOut
                    ), repeatMode = RepeatMode.Reverse
                )
            )
            Spacer(Modifier
                .graphicsLayer {
                    rotationZ = rot * 360
                    scaleX = scale
                    scaleY = scale
                }
                .clip(MaterialShapes.Cookie12Sided.toShape())
                .background(
                    MaterialTheme.colorScheme.primaryContainer
                )
                .aspectRatio(1f)
                .fillMaxWidth())
            Column(verticalArrangement = Arrangement.spacedBy((-16).dp)) {
                Text(
                    LocalStrings.current.welcomeTo,
                    fontSize = 30.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = LocalStrings.current.appName,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 80.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            var isSheetOpen by remember { mutableStateOf(false) }
            TextButton(
                onClick = { isSheetOpen = true },
                shape = MaterialTheme.shapes.extraSmall
            ) {
                LanguageChoiceSheet(isSheetOpen) { isSheetOpen = false }
                Icon(Icons.Default.Language, contentDescription = null)
                Text(
                    text = LocalStrings.current.localizedName,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 4.dp),
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
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 4.dp)
                )
                Icon(Icons.AutoMirrored.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun OnboardingThemeChooser(
    itsvm: IconThemeVM, onNext: () -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) { itsvm.fetchCustomThemes() }
    val chosen by itsvm.currentTheme.collectAsState()
    val themes by itsvm.customThemes.collectAsState()
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        LocalStrings.current.chooseIconTheme,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        LocalStrings.current.youCanAddThemeLater,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconTheme.entries.forEach {
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
                        Card(
                            modifier = Modifier.weight(1f),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = SolidColor(if (it.name == chosen) MaterialTheme.colorScheme.primary else Color.Transparent),
                                width = if (it.name == chosen) 3.dp else 1.dp
                            ),
                            shape = MaterialTheme.shapes.extraSmall,
                            onClick = { itsvm.setTheme(it.name) },
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(it.name, fontFamily = extraFamily, fontSize = 28.sp)
                                AnimatedContent(currentIndex, transitionSpec = {
                                    (slideInHorizontally() + scaleIn() + fadeIn()).togetherWith(
                                        slideOutHorizontally { it / 2 } + scaleOut() + fadeOut())
                                }) { index ->
                                    DualAsyncImage(
                                        imageModifier = Modifier.size(64.dp),
                                        dualIconResource = DualImageResource(
                                            resId = it.mapToIconResource(DefaultMoodType.entries[index]),
                                            tinted = it.tinted
                                        )
                                    )
                                }
                            }
                        }
                    }
                    themes.list.forEach { theme ->
                        OutlinedCard(
                            shape = MaterialTheme.shapes.extraSmall,
                            onClick = { itsvm.setTheme(theme.name) },
                            colors = CardDefaults.outlinedCardColors()
                                .copy(containerColor = if (theme.name == chosen) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
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
                                    val iconPath =
                                        theme.mapToIconPath(DefaultMoodType.entries[index])
                                    DualAsyncImage(
                                        imageModifier = Modifier.size(72.dp),
                                        dualIconResource = DualImageResource(
                                            IconTheme.SIMPLE.mapToIconResource(
                                                DefaultMoodType.entries[index]
                                            ), iconPath, theme.iconRounding, IconTheme.SIMPLE.tinted
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    LocalStrings.current.chooseColorTheme,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ColorSchemes.entries.forEach { theme ->
                        val themeName by MainApplication.dataStoreManager.get(SK.Theme)
                            .collectAsState("")
                        Button(
                            shape = MaterialTheme.shapes.extraSmall,
                            onClick = {
                                scope.launch {
                                    MainApplication.dataStoreManager.set(SK.Theme, theme.key)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (theme.key == themeName) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background),
                        ) {
                            Text(
                                LocalStrings.current.colorTheme(theme),
                                color = if (theme.key == themeName) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                            )
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
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 4.dp)
                )
            }
            Button(onClick = onNext, shape = MaterialTheme.shapes.extraSmall) {
                Text(
                    LocalStrings.current.navigation.finish,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 4.dp)
                )
                Icon(Icons.Default.Check, contentDescription = null)
            }
        }
    }
}