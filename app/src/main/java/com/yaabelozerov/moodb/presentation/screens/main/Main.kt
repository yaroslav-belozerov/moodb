package com.yaabelozerov.moodb.presentation.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowSizeClass
import cafe.adriel.lyricist.LocalStrings
import com.yaabelozerov.moodb.data.icons.DualImageResource
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.room.mood.RecordEntity
import com.yaabelozerov.moodb.presentation.common.DualAsyncImage
import com.yaabelozerov.moodb.presentation.common.ND
import com.yaabelozerov.moodb.presentation.screens.main.ChosenDateState.DateDialog.Editing
import com.yaabelozerov.moodb.util.display
import com.yaabelozerov.moodb.util.toDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.MonthDay
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle

sealed interface ChosenDateState {
    data object None : ChosenDateState
    sealed class DateDialog(val record: RecordEntity) : ChosenDateState {
        class Editing(rec: RecordEntity) : DateDialog(rec)
        class Creating(rec: RecordEntity) : DateDialog(rec)

        fun copy(rec: RecordEntity) = when (this) {
            is Editing -> Editing(rec)
            is Creating -> Creating(rec)
        }
    }

    fun isEditing() = this is Editing || this is ChoosingDate

    class ChoosingDate(val returnTo: DateDialog) : ChosenDateState

    data object MapOpen : ChosenDateState
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    mvm: MainVM,
    ic: Map<DefaultMoodType, DualImageResource>?,
) {
    var chosenState by remember { mutableStateOf<ChosenDateState>(ChosenDateState.None) }
    val records = mvm.records.collectAsState().value.also { println(it) }
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
            IconButton(onClick = { navController.navigate(ND.SettingsRoot) }) {
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
                chosenState = ChosenDateState.DateDialog.Creating(
                    RecordEntity(
                        0, System.currentTimeMillis(), DefaultMoodType.ANXIOUS, ""
                    )
                )
            }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = LocalStrings.current.edit.add,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            }
        }, contentPadding = PaddingValues(16.dp))
    }) { innerPadding ->
        VerticalPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), state = pager
        ) { page ->
            val cur by remember(records) {
                mutableStateOf(
                    records.entries.toList().getOrNull(page)
                )
            }
            MainContent(cur = cur, onClick = { chosenState = it }, ic = ic, mvm = mvm)

            when (chosenState) {
                is ChosenDateState.DateDialog, is ChosenDateState.ChoosingDate -> ChosenDateDialog(
                    chosenDateState = chosenState,
                    changeState = { chosenState = it },
                    mvm,
                    ic
                )

                ChosenDateState.MapOpen -> MapDialog(
                    mvm, pager, scope, onDismiss = { chosenState = ChosenDateState.None })

                ChosenDateState.None -> {}
            }
        }
    }
}

@Composable
fun MainContent(
    cur: Map.Entry<YearMonth, Map<MonthDay, RecordEntity>>?,
    onClick: (ChosenDateState) -> Unit,
    ic: Map<DefaultMoodType, DualImageResource>?,
    mvm: MainVM
) {
    val isLayoutExpanded = currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )
    cur?.let { current ->
        if (isLayoutExpanded) {
            Column(
                modifier = Modifier
                    .padding(16.dp, 16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onClick(ChosenDateState.MapOpen) }
//                    .clickable { mapShown = true }
                .padding(16.dp, 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = current.key.month.display(LocalStrings.current.localeTag),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = current.key.year.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .padding(16.dp, 16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onClick(ChosenDateState.MapOpen) }
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
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
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
                    modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center
                ) {}
            }
            (1..current.key.lengthOfMonth()).map { dayOfMonth ->
                current.value.entries.find { it.key.dayOfMonth == dayOfMonth }?.let { rec ->
                    DualAsyncImage(
                        imageModifier = Modifier
                            .size(52.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                onClick(ChosenDateState.DateDialog.Editing(rec.value))
                            }, dualIconResource = ic?.get(rec.value.type) ?: return@map
                    )
                } ?: Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            onClick(
                                ChosenDateState.DateDialog.Creating(
                                    RecordEntity(
                                        0, mvm.getTimestampForNewRecord(
                                            current.key, dayOfMonth
                                        ), DefaultMoodType.ANXIOUS, ""
                                    )
                                )
                            )
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayOfMonth.toString(), textAlign = TextAlign.Center
                    )
                }

            }
            for (i in 1..(7 - LocalDate.from(current.key.atEndOfMonth()).dayOfWeek.value)) {
                Box(
                    modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center
                ) {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChosenDateDialog(
    chosenDateState: ChosenDateState,
    changeState: (ChosenDateState) -> Unit,
    mvm: MainVM,
    ic: Map<DefaultMoodType, DualImageResource>?
) {
    val state = if (chosenDateState is ChosenDateState.ChoosingDate) {
        PickDate(chosenDateState) { changeState(it) }
        chosenDateState.returnTo
    } else {
        chosenDateState as? ChosenDateState.DateDialog
    }
    if (state == null) return

    Dialog(onDismissRequest = {
        changeState(ChosenDateState.None)
    }) {
        Card(shape = MaterialTheme.shapes.extraSmall) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val date by remember(state) {
                    mutableStateOf(
                        Instant.ofEpochMilli(
                            state.record.timestamp
                        ).toDate(
                            ZoneId.systemDefault()
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .then(if (!state.isEditing()) Modifier.clickable {
                            changeState(ChosenDateState.ChoosingDate(state))
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
                            } ${if (date.year != LocalDate.now().year) date.year else ""}"
                        }, fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (!state.isEditing()) Icon(
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
                                    changeState(
                                        state.copy(state.record.copy(type = it))
                                    )
                                }
                                .then(
                                    if (state.record.type == it) {
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
                    if (state.isEditing()) IconButton(onClick = {
                        mvm.removeRecord(state.record.recordId)
                        changeState(ChosenDateState.None)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier.padding(top = 3.dp),
                            onClick = {
                                changeState(ChosenDateState.None)
                            }) {
                            Text(
                                text = LocalStrings.current.navigation.cancel,
                                style = MaterialTheme.typography.titleMediumEmphasized
                            )
                        }
                        Button(
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier.padding(top = 3.dp),
                            onClick = {
                                if (!state.isEditing()) {
                                    mvm.insertRecord(state.record) { month, year ->
//                                                    val key =
//                                                        records.find { it.first.month.value == month && it.first.year == year }
//                                                    withContext(scope.coroutineContext) {
//                                                        if (key != null) pager.animateScrollToPage(
//                                                            records.indexOfFirst { key == it } - 1
//                                                        )
//                                                    }
                                    }
                                } else {
                                    state.record.run {
                                        mvm.modifyRecord(
                                            recordId, type
                                        )
                                    }
                                }
                                changeState(ChosenDateState.None)
                            }) {
                            Text(
                                text = LocalStrings.current.edit.save,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PickDate(choosing: ChosenDateState.ChoosingDate?, setState: (ChosenDateState) -> Unit) {
    if (choosing == null) return

    val picker =
        rememberDatePickerState(initialSelectedDateMillis = choosing.returnTo.record.timestamp)
    DatePickerDialog(
        shape = MaterialTheme.shapes.extraSmall,
        onDismissRequest = { setState(choosing.returnTo) },
        dismissButton = {
            TextButton(shape = MaterialTheme.shapes.extraSmall, onClick = {
                setState(choosing.returnTo)
            }) {
                Text(
                    text = LocalStrings.current.navigation.cancel,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.padding(horizontal = 8.dp),
                shape = MaterialTheme.shapes.extraSmall,
                onClick = {
                    setState(
                        choosing.returnTo.copy(
                            choosing.returnTo.record.copy(
                                timestamp = picker.selectedDateMillis
                                    ?: choosing.returnTo.record.timestamp
                            )
                        )
                    )
                }) {
                Text(
                    text = LocalStrings.current.edit.save,
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }) {
        DatePicker(state = picker, headline = {
            val date by remember {
                derivedStateOf {
                    Instant.ofEpochMilli(
                        picker.selectedDateMillis ?: choosing.returnTo.record.timestamp
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
                    } ${if (date.year == LocalDate.now().year) "" else date.year}"
                },
                fontSize = 32.sp,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 32.dp)
            )
        })
    }
}

@Composable
fun MapDialog(mvm: MainVM, pager: PagerState, scope: CoroutineScope, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
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
                ) { onDismiss() }
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(0.dp, 16.dp)) {
            items(grouped.keys.toList()) { it ->
                Column {
                    Text(
                        text = it.toString(), fontSize = 24.sp, color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        grouped[it]?.map {
                            Card(
                                shape = MaterialTheme.shapes.extraSmall, onClick = {
                                    scope.launch {
                                        pager.animateScrollToPage(it.page)
                                    }
                                    onDismiss()
                                }, colors = CardDefaults.cardColors(
                                    containerColor = if (it.hasRecords) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
                                )
                            ) {
                                Text(
                                    modifier = Modifier.padding(8.dp), text = it.monthName
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}