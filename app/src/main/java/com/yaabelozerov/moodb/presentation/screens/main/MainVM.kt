package com.yaabelozerov.moodb.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.room.mood.RecordDao
import com.yaabelozerov.moodb.data.room.mood.RecordEntity
import com.yaabelozerov.moodb.di.MainApplication
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
import timber.log.Timber
import java.time.LocalDateTime
import java.time.MonthDay
import java.time.OffsetTime
import java.time.YearMonth
import java.time.ZonedDateTime

data class GroupedMonth(
    val page: Int,
    val monthName: String,
    val hasRecords: Boolean
)

class MainVM(
    private val dao: RecordDao = MainApplication.recordDao
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
                        }
                    }
                    return@collect
                }

                val zone = ZoneId.systemDefault()

                val yearMonths = records.map {
                    Instant.ofEpochMilli(it.timestamp).atZone(zone).let { dt -> YearMonth.from(dt) }
                }

                val firstMonth = yearMonths.minOrNull()
                val lastMonth = yearMonths.maxOrNull()
                if (firstMonth == null || lastMonth == null) {
                    Timber.e("No first or last month, $yearMonths, $records")
                    return@collect
                }

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

    fun getRecordsGroupedByYear(localeTag: String): Map<Int, List<GroupedMonth>> {
        val mp = mutableMapOf<Int, List<GroupedMonth>>()
        _records.value.keys.forEachIndexed { index, it ->
            mp[it.year] = (mp[it.year] ?: emptyList()) + GroupedMonth(index, it.month.display(localeTag),
                _records.value[it]?.values?.isNotEmpty() == true
            )
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