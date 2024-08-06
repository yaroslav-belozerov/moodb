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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconThemeTopBar(
    scroll: TopAppBarScrollBehavior,
    onBack: (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit
) {
    TopBar(
        name = stringResource(id = R.string.icon_theme),
        scroll = scroll,
        onBack = onBack,
        actions = actions)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun IconTheme(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    chosen: String,
    themes: ThemeList,
    onBack: (() -> Unit)?,
    onCreateTheme: () -> Unit,
    onChooseIcon: (String, DefaultMoodType) -> Unit,
    onChangeRounding: (String, Float) -> Unit,
    onSavePackName: (String, String) -> Unit,
    onSetCurrentTheme: (String) -> Unit,
    onRemoveIcon: (String, DefaultMoodType, String) -> Unit,
    onRemoveTheme: (String) -> Unit,
) {
    val scroll = exitUntilCollapsedScrollBehavior()
    Scaffold(topBar = {
        IconThemeTopBar(
            scroll = scroll,
            onBack = onBack,
            actions = {
                IconButton(onClick = {
                    onCreateTheme()
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
                DefaultTheme(it, it.name == chosen, onSetCurrentTheme = onSetCurrentTheme)
            }
            items(themes.list) {
                CustomTheme(
                    imageLoader = imageLoader,
                    it.name == chosen,
                    theme = it,
                    default = IconTheme.SIMPLE,
                    onChooseIcon = onChooseIcon,
                    onChangeRounding = onChangeRounding,
                    onSavePackName = onSavePackName,
                    onSetCurrentTheme = onSetCurrentTheme,
                    onRemoveTheme = onRemoveTheme,
                    onRemoveIcon = onRemoveIcon
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
            Text(text = stringResource(id = theme.nameRes), fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 8
            ) {
                DefaultMoodType.entries.map { type ->
                    SubcomposeAsyncImage(
                        model = theme.mapToIconResource(type),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomTheme(
    imageLoader: ImageLoader,
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
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { editing = true }
                        .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(text = theme.name, fontSize = 32.sp)
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                    }
                } else {
                    var newText by remember {
                        mutableStateOf(theme.name)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextField(value = newText,
                            onValueChange = { newText = it },
                            trailingIcon = {
                                IconButton(onClick = {
                                    onSavePackName(theme.name, newText)
                                    editing = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Check, contentDescription = null
                                    )
                                }
                            },
                            textStyle = LocalTextStyle.current.copy(fontSize = 32.sp)
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
                                .alpha(0.2f)
                                .clickable { onChooseIcon(theme.name, type) }
                            else Modifier.clickable { onRemoveIcon(theme.name, type, iconPath) })
                    ) {
                        DualAsyncImage(
                            imageModifier = Modifier.size(64.dp),
                            imageLoader = imageLoader,
                            dualIconResource = DualImageResource(
                                default.mapToIconResource(type), iconPath, theme.iconRounding
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