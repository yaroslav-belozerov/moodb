package com.yaabelozerov.moodb.presentation.screens.moodedit

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.lyricist.LocalStrings
import com.yaabelozerov.moodb.R
import com.yaabelozerov.moodb.data.model.Category
import com.yaabelozerov.moodb.data.model.MoodType
import com.yaabelozerov.moodb.presentation.common.TopBar
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodEdit(
    modifier: Modifier = Modifier,
    type: MoodType,
    onBack: () -> Unit,
    onSetDefaultType: (MoodType) -> Unit,
    onSetNewType: (MoodType, String, Category) -> Unit
) {
    val name = type.customName ?: LocalStrings.current.moodType(type.defaultMoodType)
    var txt by remember {
        mutableStateOf(name)
    }
    var category by remember {
        mutableStateOf(
            type.customCategory ?: type.defaultMoodType.category
        )
    }
    val scroll = exitUntilCollapsedScrollBehavior()
    Scaffold(topBar = {
        TopBar(name = "", scroll = scroll, onBack = onBack, actions = {
            IconButton(onClick = {
                onSetDefaultType(type)
                onBack()
            }) {
                Icon(
                    imageVector = Icons.Default.Refresh, contentDescription = null
                )
            }
        })
    }) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(32.dp, 8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextField(
                        value = txt,
                        shape = MaterialTheme.shapes.extraSmall,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { txt = it },
                        textStyle = TextStyle.Default.copy(fontSize = 20.sp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        )
                    )
                }
                CategoryList(category = category, onValueChange = { category = it })
            }
            Button(shape = MaterialTheme.shapes.extraSmall, onClick = {
                onSetNewType(type, txt, category)
                onBack()
            }, modifier = Modifier.fillMaxWidth()) {
                Text(text = LocalStrings.current.edit.save, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 8.dp))
                Icon(
                    imageVector = Icons.Default.Check, contentDescription = null
                )
            }
        }
    }
}

@Composable
fun CategoryList(category: Category, onValueChange: (Category) -> Unit) {
    CategoryRow(
        name = LocalStrings.current.mood.happy, value = category.happy
    ) { new -> onValueChange(category.copy(happy = new)) }
    CategoryRow(
        name = LocalStrings.current.mood.energetic, value = category.energetic
    ) { new -> onValueChange(category.copy(energetic = new)) }
    CategoryRow(
        name = LocalStrings.current.mood.neutral, value = category.neutral
    ) { new -> onValueChange(category.copy(neutral = new)) }
    CategoryRow(
        name = LocalStrings.current.mood.sad, value = category.sad
    ) { new -> onValueChange(category.copy(sad = new)) }
    CategoryRow(
        name = LocalStrings.current.mood.angry, value = category.angry
    ) { new -> onValueChange(category.copy(angry = new)) }
}

@Composable
fun CategoryRow(name: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Text(text = name, fontSize = 20.sp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = (value * 10).roundToInt().toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp)
            )
            Slider(value = value, onValueChange = onValueChange, steps = 9)
        }
    }
}