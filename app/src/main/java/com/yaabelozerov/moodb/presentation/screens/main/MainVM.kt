package com.yaabelozerov.moodb.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.room.mood.RecordDao
import com.yaabelozerov.moodb.data.room.mood.RecordEntity
import com.yaabelozerov.moodb.di.BaseApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import com.yaabelozerov.moodb.util.display
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.plus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.MonthDay
import java.time.OffsetTime
import java.time.YearMonth
import java.time.ZonedDateTime

@HiltViewModel
class MainVM @Inject constructor(
    private val dao: RecordDao
) : ViewModel() {
    private val _records = MutableStateFlow(emptyMap<YearMonth, Map<MonthDay, RecordEntity>>())
    val records = _records.asStateFlow()

    fun groupRecordsByMonthDay() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getAll().collect { records ->
                if (records.isEmpty()) {
                    _records.update {
                        buildMap {
                            val now = YearMonth.now()
                            put(now.minusMonths(1), emptyMap())
                            put(now, emptyMap())
                            put(now.plusMonths(1), emptyMap())
                        }
                    }
                    return@collect
                }

                val zone = ZoneId.systemDefault()

                val yearMonths = records.map {
                    Instant.ofEpochMilli(it.timestamp).atZone(zone).let { dt -> YearMonth.from(dt) }
                }

                val firstMonth = yearMonths.minOrNull() ?: return@collect
                val lastMonth = yearMonths.maxOrNull() ?: return@collect

                val recordsByMonthDay: Map<YearMonth, Map<MonthDay, RecordEntity>> = records.groupBy { record ->
                    val dt = Instant.ofEpochMilli(record.timestamp).atZone(zone).toLocalDate()
                    YearMonth.from(dt)
                }.mapValues { entry ->
                    entry.value.associateBy { record ->
                        val dt = Instant.ofEpochMilli(record.timestamp).atZone(zone).toLocalDate()
                        MonthDay.from(dt)
                    }
                }

                _records.update {
                    generateSequence(firstMonth) { ym ->
                        if (ym >= lastMonth) null else ym.plusMonths(1)
                    }.plus(lastMonth) // ensure last month is included
                        .map { ym ->
                            ym to (recordsByMonthDay[ym] ?: emptyMap())
                        }.toMap()
                }
            }
        }
    }

    fun modifyRecord(id: Long, type: DefaultMoodType) {
        viewModelScope.launch {
            dao.updateType(id, type)
            groupRecordsByMonthDay()
        }
    }

    fun substituteTime(timestamp: Long): Long {
        val z = ZoneId.systemDefault()
        val local = Instant.ofEpochMilli(timestamp).atZone(z).toLocalDateTime()
        val now = LocalDateTime.now()
        return now.withYear(local.year).withMonth(local.month.value)
            .withDayOfMonth(local.dayOfMonth).toInstant(OffsetTime.now().offset).toEpochMilli()
    }

    fun insertRecord(
        currentEdit: RecordEntity, callback: suspend (Int, Int) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            dao.insertRecord(currentEdit)
            val d = Instant.ofEpochMilli(currentEdit.timestamp).atZone(ZoneId.systemDefault())
            groupRecordsByMonthDay()
            callback(d.month.value, d.year)
        }
    }

    fun getTimestampForNewRecord(month: YearMonth, day: Int): Long {
        val (hour, minute) = LocalDateTime.now().let { it.hour to it.minute }
        return LocalDateTime.from(month.atDay(day).atTime(hour, minute)).toInstant(ZonedDateTime.now().offset).toEpochMilli()
    }

    fun getRecordsGroupedByYear(localeTag: String): Map<Int, List<Pair<Int, String>>> {
        val mp = mutableMapOf<Int, List<Pair<Int, String>>>()
        _records.value.keys.forEachIndexed { index, it ->
            mp[it.year] = (mp[it.year] ?: emptyList()) + (index to it.month.display(localeTag))
        }
        return mp
    }

    fun removeRecord(id: Long) {
        viewModelScope.launch {
            dao.removeRecord(id)
            groupRecordsByMonthDay()
        }
    }
}