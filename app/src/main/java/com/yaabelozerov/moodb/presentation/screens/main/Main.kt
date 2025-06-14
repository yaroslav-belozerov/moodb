package com.yaabelozerov.moodb.presentation.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import cafe.adriel.lyricist.LocalStrings
import com.yaabelozerov.moodb.data.icons.DualImageResource
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.room.mood.RecordEntity
import com.yaabelozerov.moodb.presentation.common.DualAsyncImage
import com.yaabelozerov.moodb.presentation.common.ND
import com.yaabelozerov.moodb.util.display
import com.yaabelozerov.moodb.util.toDate
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    mvm: MainVM,
    ic: Map<DefaultMoodType, DualImageResource>?,
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
    val records = mvm.records.collectAsState().value.also { println(it) }
    var currentEdit by remember {
        mutableStateOf<RecordEntity?>(null)
    }
    var mapShown by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val lastIndex = remember(records) { records.size - 1 }
    if (lastIndex < 0) return
    val pager = rememberPagerState(initialPage = lastIndex, pageCount = { records.size })
    LaunchedEffect(lastIndex) {
        scope.launch {
            pager.scrollToPage(lastIndex)
        }
    }
    Scaffold(bottomBar = {
        BottomAppBar(actions = {
            IconButton(onClick = { navController.navigate(ND.SettingsScreen.route) }) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null)
            }
            AnimatedVisibility(lastIndex != pager.currentPage) {
                IconButton(onClick = {
                    scope.launch {
                        pager.animateScrollToPage(lastIndex)
                    }
                }) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(shape = MaterialTheme.shapes.extraSmall, onClick = {
                pickerDate = System.currentTimeMillis()
                creating = true
                editing = false
                currentEdit = RecordEntity(0, pickerDate, DefaultMoodType.ANXIOUS, "")
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = LocalStrings.current.edit.add,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            }
        }, contentPadding = PaddingValues(16.dp))
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            VerticalPager(modifier = Modifier.fillMaxWidth(), state = pager) { page ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    val cur by remember(records) {
                        mutableStateOf(
                            records.entries.toList().getOrNull(page)
                        )
                    }
                    cur?.let { current ->
                        Row(
                            modifier = Modifier
                                .padding(16.dp, 16.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { mapShown = true }
                                .padding(16.dp, 16.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = current.key.month.display(LocalStrings.current.localeTag),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = current.key.year.toString(),
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
                            for (i in 1..LocalDate.from(current.key.atDay(1)).dayOfWeek.value) {
                                Box(
                                    modifier = Modifier.size(48.dp),
                                    contentAlignment = Alignment.Center
                                ) {}
                            }
                            (1..current.key.lengthOfMonth()).map { dayOfMonth ->
                                current.value.entries.find { it.key.dayOfMonth == dayOfMonth }?.let { rec ->
                                    DualAsyncImage(
                                        imageModifier = Modifier
                                            .size(52.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .clickable {
                                                creating = true
                                                editing = true
                                                pickerDate = mvm.substituteTime(
                                                    rec.value.timestamp
                                                )
                                                currentEdit = rec.value
                                            }, dualIconResource = ic?.get(rec.value.type) ?: return@map
                                    )
                                } ?: Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable {
                                            creating = true
                                            editing = false
                                            pickerDate = mvm.getTimestampForNewRecord(
                                                current.key, dayOfMonth
                                            )
                                            currentEdit = RecordEntity(
                                                0, pickerDate, DefaultMoodType.ANXIOUS, ""
                                            )
                                        }, contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayOfMonth.toString(), textAlign = TextAlign.Center
                                    )
                                }

                            }
                            for (i in 1..(7-LocalDate.from(current.key.atEndOfMonth()).dayOfWeek.value)) {
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
                val strings = LocalStrings.current
                val grouped by remember {
                    derivedStateOf {
                        mvm.getRecordsGroupedByYear(strings.localeTag)
                    }
                }
                val interactionSource = remember { MutableInteractionSource() }
                LazyColumn(
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource, indication = null
                        ) { mapShown = false }
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(0.dp, 16.dp)) {
                    items(grouped.keys.toList()) { it ->
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
                                grouped[it]?.map {
                                    Card(shape = MaterialTheme.shapes.extraSmall, modifier = Modifier.clickable {
                                        scope.launch {
                                            pager.animateScrollToPage(it.first)
                                        }
                                        mapShown = false
                                    }) {
                                        Text(
                                            modifier = Modifier.padding(8.dp), text = it.second
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
                    Card(shape = MaterialTheme.shapes.extraSmall) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val date by remember {
                                derivedStateOf {
                                    Instant.ofEpochMilli(
                                        pickerDate
                                    ).toDate(
                                        ZoneId.systemDefault()
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .then(if (!editing) Modifier.clickable {
                                        pickerShown = true
                                    } else Modifier)
                                    .padding(8.dp)) {
                                Text(
                                    text = if (date.dayOfYear == LocalDate.now().dayOfYear && date.year == LocalDate.now().year) {
                                        LocalStrings.current.today
                                    } else {
                                        "${date.dayOfMonth} ${
                                            date.month.display(
                                                LocalStrings.current.localeTag, TextStyle.FULL
                                            )
                                        }"
                                    }, fontSize = 32.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (!editing) Icon(
                                    imageVector = Icons.Default.Edit, contentDescription = null
                                )
                            }
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                maxItemsInEachRow = 4,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                DefaultMoodType.entries.map {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(MaterialTheme.shapes.medium)
                                            .clickable {
                                                currentEdit = currentEdit?.copy(type = it)
                                            }
                                            .then(
                                                if (currentEdit?.type == it) {
                                                    Modifier.border(
                                                        BorderStroke(
                                                            3.dp, MaterialTheme.colorScheme.primary
                                                        ), MaterialTheme.shapes.medium
                                                    )
                                                } else Modifier
                                            ), contentAlignment = Alignment.Center) {
                                        DualAsyncImage(
                                            imageModifier = Modifier
                                                .padding(8.dp)
                                                .size(56.dp),
                                            dualIconResource = ic?.get(it) ?: return@FlowRow
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
                                    TextButton(
                                        shape = MaterialTheme.shapes.extraSmall,
                                        modifier = Modifier.padding(top = 2.dp),
                                        onClick = {
                                            creating = false
                                        }) {
                                        Text(text = LocalStrings.current.navigation.cancel)
                                    }
                                    Button(
                                        shape = MaterialTheme.shapes.extraSmall,
                                        modifier = Modifier.padding(top = 2.dp),
                                        onClick = {
                                            if (!editing) {
                                                mvm.insertRecord(currentEdit!!) { month, year ->
//                                                    val key =
//                                                        records.find { it.first.month.value == month && it.first.year == year }
//                                                    withContext(scope.coroutineContext) {
//                                                        if (key != null) pager.animateScrollToPage(
//                                                            records.indexOfFirst { key == it } - 1
//                                                        )
//                                                    }
                                                }
                                            } else {
                                                mvm.modifyRecord(
                                                    currentEdit!!.recordId, currentEdit!!.type
                                                )
                                            }
                                            creating = false
                                        }) {
                                        Text(text = LocalStrings.current.edit.save)
                                    }
                                }
                            }
                        }
                    }
                }
                if (pickerShown) {
                    DatePickerDialog(
                        shape = MaterialTheme.shapes.extraSmall,
                        onDismissRequest = { pickerShown = false },
                        dismissButton = {
                            TextButton(shape = MaterialTheme.shapes.extraSmall, onClick = {
                                pickerShown = false
                            }) {
                                Text(text = LocalStrings.current.navigation.cancel)
                            }
                        },
                        confirmButton = {
                            Button(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                shape = MaterialTheme.shapes.extraSmall,
                                onClick = {
                                    pickerShown = false
                                    pickerDate = picker.selectedDateMillis ?: pickerDate
                                    currentEdit =
                                        currentEdit?.copy(timestamp = picker.selectedDateMillis!!)
                                            ?: currentEdit
                                }) {
                                Text(text = LocalStrings.current.edit.save)
                            }
                        }) {
                        DatePicker(state = picker, headline = {
                            val date by remember {
                                derivedStateOf {
                                    Instant.ofEpochMilli(
                                        picker.selectedDateMillis ?: pickerDate
                                    ).toDate(
                                        ZoneId.systemDefault()
                                    )
                                }
                            }
                            Text(
                                text = if (date.dayOfYear == LocalDate.now().dayOfYear && date.year == LocalDate.now().year) {
                                    LocalStrings.current.today
                                } else {
                                    "${date.dayOfMonth} ${
                                        date.month.display(
                                            LocalStrings.current.localeTag, TextStyle.FULL
                                        )
                                    }"
                                },
                                fontSize = 32.sp,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(start = 32.dp)
                            )
                        })
                    }
                }
            }
        }
    }
}