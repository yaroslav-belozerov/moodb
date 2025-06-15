package com.yaabelozerov.moodb.presentation.screens.icontheme

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.lyricist.LocalStrings
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import com.yaabelozerov.moodb.R
import com.yaabelozerov.moodb.data.icons.DualImageResource
import com.yaabelozerov.moodb.data.model.CustomIconTheme
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.model.IconTheme
import com.yaabelozerov.moodb.data.model.ThemeList
import com.yaabelozerov.moodb.presentation.common.DualAsyncImage
import com.yaabelozerov.moodb.presentation.common.TopBar
import com.yaabelozerov.moodb.presentation.theme.extraFamily
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconThemeTopBar(
    scroll: TopAppBarScrollBehavior, onBack: (() -> Unit)?, actions: @Composable RowScope.() -> Unit
) {
    TopBar(
        name = LocalStrings.current.settings.iconTheme,
        scroll = scroll,
        onBack = onBack,
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun IconTheme(
    modifier: Modifier = Modifier,
    onExit: () -> Unit,
    itsvm: IconThemeVM,
) {
    LaunchedEffect(Unit) { itsvm.fetchCustomThemes() }
    val scroll = exitUntilCollapsedScrollBehavior()
    val chosen by itsvm.currentTheme.collectAsState()
    val themes by itsvm.customThemes.collectAsState()
    val strings = LocalStrings.current
    Scaffold(topBar = {
        IconThemeTopBar(scroll = scroll, onBack = onExit, actions = {
            IconButton(onClick = {
                itsvm.createTheme(strings.theme)
            }) {
                Icon(
                    imageVector = Icons.Default.Add, contentDescription = null
                )
            }
        })
    }) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .nestedScroll(scroll.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(IconTheme.entries) {
                DefaultTheme(
                    it,
                    it.name == chosen,
                    onSetCurrentTheme = itsvm::setTheme,
                )
            }
            items(themes.list) {
                CustomTheme(
                    it.name == chosen,
                    theme = it,
                    default = IconTheme.SIMPLE,
                    onChooseIcon = { packName, type ->
                        itsvm.setTypeAndSetter(packName, type)
                        itsvm.launchIconPicker()
                    },
                    onChangeRounding = itsvm::setRounding,
                    onSavePackName = itsvm::setThemeName,
                    onSetCurrentTheme = itsvm::setTheme,
                    onRemoveTheme = itsvm::removeTheme,
                    onRemoveIcon = itsvm::removeFile
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DefaultTheme(
    theme: IconTheme,
    isChosen: Boolean = false,
    onSetCurrentTheme: (String) -> Unit,
) {
    Card(
        onClick = { if (!isChosen) onSetCurrentTheme(theme.name) },
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
            .then(
                if (isChosen) Modifier.border(
                    3.dp, MaterialTheme.colorScheme.primary, CardDefaults.shape
                )
                else Modifier
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = theme.name, fontFamily = extraFamily, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 8
            ) {
                DefaultMoodType.entries.map { type ->
                    DualAsyncImage(
                        imageModifier = Modifier.size(36.dp),
                        dualIconResource = DualImageResource(
                            resId = theme.mapToIconResource(type), tinted = theme.tinted
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomTheme(
    isChosen: Boolean,
    theme: CustomIconTheme,
    default: IconTheme,
    onChooseIcon: (String, DefaultMoodType) -> Unit,
    onChangeRounding: (String, Float) -> Unit,
    onSavePackName: (String, String) -> Unit,
    onSetCurrentTheme: (String) -> Unit,
    onRemoveTheme: (String) -> Unit,
    onRemoveIcon: (String, DefaultMoodType, String) -> Unit
) {
    Card(
        onClick = { if (!isChosen) onSetCurrentTheme(theme.name) },
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
            .then(
                if (isChosen) Modifier.border(
                    3.dp, MaterialTheme.colorScheme.primary, CardDefaults.shape
                )
                else Modifier
            )
    ) {
        var editing by remember {
            mutableStateOf(false)
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!editing) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { editing = true }
                        .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = theme.name, fontFamily = extraFamily, fontSize = 36.sp, modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp))
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(32.dp))
                    }
                } else {
                    var newText by remember {
                        mutableStateOf(theme.name)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        val fr = remember { FocusRequester() }
                        LaunchedEffect(editing) { if (editing) fr.requestFocus() }
                        TextField(
                            value = newText,
                            onValueChange = { newText = it }, modifier = Modifier.weight(1f).focusRequester(fr),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape = MaterialTheme.shapes.extraSmall,
                            trailingIcon = {
                                IconButton(onClick = {
                                    onSavePackName(theme.name, newText)
                                    editing = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Check, contentDescription = null
                                    )
                                }
                            }, textStyle = MaterialTheme.typography.titleLarge
                        )
                        IconButton(onClick = {
                            editing = false
                            onRemoveTheme(theme.name)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete, contentDescription = null
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 4
            ) {
                DefaultMoodType.entries.map { type ->
                    val iconPath = theme.mapToIconPath(type)
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(72.dp)
                            .weight(1f)
                            .clip(MaterialTheme.shapes.medium)
                            .then(if (iconPath == null) Modifier
                                .clickable { onChooseIcon(theme.name, type) }
                            else Modifier.clickable { onRemoveIcon(theme.name, type, iconPath) })
                    ) {
                        DualAsyncImage(
                            imageModifier = Modifier.size(64.dp),
                            dualIconResource = DualImageResource(
                                default.mapToIconResource(type), iconPath, theme.iconRounding, default.tinted
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Face, contentDescription = null)
                Slider(
                    value = theme.iconRounding,
                    onValueChange = { onChangeRounding(theme.name, it) })
            }
        }
    }
}