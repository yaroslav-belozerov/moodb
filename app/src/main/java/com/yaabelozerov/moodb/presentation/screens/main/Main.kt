package com.yaabelozerov.moodb.presentation.screens.main

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import com.yaabelozerov.moodb.R
import com.yaabelozerov.moodb.data.icons.DualImageResource
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.room.mood.RecordEntity
import com.yaabelozerov.moodb.presentation.common.DualAsyncImage
import com.yaabelozerov.moodb.presentation.common.ND
import com.yaabelozerov.moodb.util.display
import com.yaabelozerov.moodb.util.toDate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    mvm: MainVM,
    ic: Map<DefaultMoodType, DualImageResource>,
    imageLoader: ImageLoader
) {
    var creating by remember {
        mutableStateOf(false)
    }
    var editing by remember {
        mutableStateOf(false)
    }
    var pickerDate by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }
    val records = mvm.records.collectAsState().value
    var currentEdit by remember {
        mutableStateOf<RecordEntity?>(null)
    }
    var mapShown by remember {
        mutableStateOf(false)
    }
    val sf = mvm.showFirst.collectAsState().value
    val scope = rememberCoroutineScope()
    Scaffold(bottomBar = {
        BottomAppBar(actions = {
            IconButton(onClick = { navController.navigate(ND.SettingsScreen.route) }) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null)
            }
            IconButton(onClick = { navController.navigate(ND.MainScreen.route) }) {
                Icon(imageVector = Icons.Default.Home, contentDescription = null)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                pickerDate = System.currentTimeMillis()
                creating = true
                editing = false
                currentEdit = RecordEntity(0, pickerDate, DefaultMoodType.ANXIOUS, "")
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(id = R.string.add))
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            }
        }, contentPadding = PaddingValues(16.dp))
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Crossfade(targetState = sf) { showFirst ->
                if (showFirst != null) {
                    val pager = rememberPagerState(
                        initialPage = showFirst - 1,
                        pageCount = { records.size })
                    VerticalPager(modifier = Modifier.fillMaxWidth(), state = pager) { page ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            val current = records.keys.find { it.first == page + 1 }
                            if (current != null) {
                                Row(modifier = Modifier
                                    .padding(16.dp, 16.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable { mapShown = true }
                                    .padding(16.dp, 16.dp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = current.second.third,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = current.second.fourth,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp, 0.dp),
                                    maxItemsInEachRow = 7,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    (1..current.second.second).map {
                                        Box(
                                            modifier = Modifier.size(48.dp),
                                            contentAlignment = Alignment.Center
                                        ) {}
                                    }
                                    (1..current.second.first).map {
                                        if (records[current]?.get(it) != null) {
                                            DualAsyncImage(
                                                imageModifier = Modifier
                                                    .size(52.dp)
                                                    .clip(MaterialTheme.shapes.medium)
                                                    .clickable {
                                                        creating = true
                                                        editing = true
                                                        pickerDate = mvm.substituteTime(
                                                            records[current]?.get(
                                                                it
                                                            )!!.timestamp
                                                        )
                                                        currentEdit = records[current]?.get(
                                                            it
                                                        )!!
                                                    },
                                                imageLoader = imageLoader,
                                                dualIconResource = ic[records[current]?.get(it)!!.type]!!
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(52.dp)
                                                    .clip(MaterialTheme.shapes.medium)
                                                    .clickable {
                                                        creating = true
                                                        editing = false
                                                        pickerDate = mvm.getTimestampForNewRecord(
                                                            current, it
                                                        )
                                                        currentEdit = RecordEntity(
                                                            0,
                                                            pickerDate,
                                                            DefaultMoodType.ANXIOUS,
                                                            ""
                                                        )
                                                    }, contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = it.toString(),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                    (1..(7 - (current.second.first + current.second.second) % 7)).map {
                                        Box(
                                            modifier = Modifier.size(48.dp),
                                            contentAlignment = Alignment.Center
                                        ) {}
                                    }
                                }
                            }
                        }
                    }
                    if (mapShown) Dialog(onDismissRequest = { mapShown = false }) {
                        val grouped = mvm.getGroupedRecords()
                        val interactionSource = remember { MutableInteractionSource() }
                        LazyColumn(modifier = Modifier
                            .clickable(
                                interactionSource = interactionSource, indication = null
                            ) { mapShown = false }
                            .fillMaxSize()
                            .padding(0.dp, 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(grouped.keys.toList()) {
                                Column {
                                    Text(
                                        text = it.toString(),
                                        fontSize = 24.sp,
                                        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        grouped[it]!!.map {
                                            Card(modifier = Modifier.clickable {
                                                scope.launch {
                                                    pager.animateScrollToPage(it.first - 1)
                                                }
                                                mapShown = false
                                            }) {
                                                Text(
                                                    modifier = Modifier.padding(8.dp),
                                                    text = it.second
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (creating) {
                        val picker = rememberDatePickerState(initialSelectedDateMillis = pickerDate)
                        var pickerShown by remember { mutableStateOf(false) }
                        Dialog(onDismissRequest = {
                            creating = false
                        }) {
                            Card {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    val date = Instant.ofEpochMilli(
                                        pickerDate
                                    ).toDate(
                                        ZoneId.systemDefault()
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(MaterialTheme.shapes.medium)
                                            .then(if (!editing) Modifier.clickable {
                                                pickerShown = true
                                            } else Modifier)
                                            .padding(8.dp)) {
                                        Text(
                                            text = if (date.dayOfYear == LocalDate.now().dayOfYear && date.year == LocalDate.now().year) {
                                                stringResource(id = R.string.today)
                                            } else {
                                                "${date.dayOfMonth} ${
                                                    date.month.display(
                                                        TextStyle.FULL
                                                    )
                                                }"
                                            }, fontSize = 32.sp
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        if (!editing) Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null
                                        )
                                    }
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        maxItemsInEachRow = 4,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        DefaultMoodType.entries.map {
                                            Box(modifier = Modifier
                                                .weight(1f)
                                                .clip(MaterialTheme.shapes.medium)
                                                .clickable {
                                                    currentEdit = currentEdit?.copy(type = it)
                                                }
                                                .then(
                                                    if (currentEdit?.type == it) {
                                                        Modifier.border(
                                                            BorderStroke(
                                                                3.dp,
                                                                MaterialTheme.colorScheme.primary
                                                            ), MaterialTheme.shapes.medium
                                                        )
                                                    } else Modifier
                                                ), contentAlignment = Alignment.Center) {
                                                DualAsyncImage(
                                                    imageModifier = Modifier
                                                        .padding(8.dp)
                                                        .size(56.dp),
                                                    imageLoader = imageLoader,
                                                    dualIconResource = ic[it]!!
                                                )
                                            }
                                        }
                                    }
                                    Row {
                                        if (editing) IconButton(onClick = {
                                            mvm.removeRecord(currentEdit!!.recordId)
                                            creating = false
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null
                                            )
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedButton(onClick = {
                                                creating = false
                                            }) {
                                                Text(text = stringResource(id = R.string.cancel))
                                            }
                                            Button(onClick = {
                                                if (!editing) {
                                                    mvm.insertRecord(currentEdit!!) { month, year ->
                                                        val key =
                                                            records.keys.find { it.third.first == month && it.third.second == year }
                                                        if (key != null) pager.animateScrollToPage(
                                                            key.first - 1
                                                        )
                                                    }
                                                } else {
                                                    mvm.modifyRecord(
                                                        currentEdit!!.recordId, currentEdit!!.type
                                                    )
                                                }
                                                creating = false
                                            }) {
                                                Text(text = stringResource(id = R.string.save))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (pickerShown) {
                            DatePickerDialog(onDismissRequest = { pickerShown = false },
                                dismissButton = {
                                    OutlinedButton(onClick = {
                                        pickerShown = false
                                    }) {
                                        Text(text = stringResource(id = R.string.cancel))
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        pickerShown = false
                                        pickerDate = picker.selectedDateMillis ?: pickerDate
                                        currentEdit =
                                            currentEdit?.copy(timestamp = picker.selectedDateMillis!!)
                                                ?: currentEdit
                                    }) {
                                        Text(text = stringResource(id = R.string.save))
                                    }
                                }) {
                                DatePicker(state = picker)
                            }
                        }
                    }
                }
            }
        }
    }
}