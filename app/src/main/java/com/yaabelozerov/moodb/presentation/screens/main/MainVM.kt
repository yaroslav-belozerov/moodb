package com.yaabelozerov.moodb.presentation.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import com.yaabelozerov.moodb.data.room.mood.RecordDao
import com.yaabelozerov.moodb.data.room.mood.RecordEntity
import com.yaabelozerov.moodb.presentation.common.Four
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import com.yaabelozerov.moodb.util.display
import com.yaabelozerov.moodb.util.toDate
import kotlinx.coroutines.plus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetTime
import java.time.Period
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.TemporalUnit
import java.util.Calendar
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds

typealias Month = Four<Int, Int, String, String>
typealias PagedMonth = Triple<Int, Month, MonthYear>
typealias MonthYear = Pair<Int, Int>

@HiltViewModel
class MainVM @Inject constructor(
    private val dao: RecordDao
) : ViewModel() {
    private val _records = MutableStateFlow(emptyMap<PagedMonth, Map<Int, RecordEntity?>>())
    val records = _records.asStateFlow()
    private val _showFirst = MutableStateFlow<Int?>(null)
    val showFirst = _showFirst.asStateFlow()

    init {
        fetchMonths()
    }

    fun fetchMonths() {
        viewModelScope.launch {
            dao.getAll().collect { list ->
                val z = ZoneId.systemDefault()
                val recordsNew = mutableMapOf<PagedMonth, Map<Int, RecordEntity?>>()
                val currentDate = LocalDate.now()


                if (list.isEmpty()) {
                    val currMinus = currentDate.minusMonths(1)
                    val currPlus = currentDate.plusMonths(1)
                    listOf(currMinus, currentDate, currPlus).forEachIndexed { i, it ->
                        recordsNew[PagedMonth(
                            i + 1, Month(
                                it.month.length(false),
                                it.minusDays(it.dayOfMonth.toLong()).dayOfWeek.value,
                                it.month.display(),
                                it.year.toString()
                            ), MonthYear(it.month.value, it.year)
                        )] = emptyMap()
                    }
                    _records.update { recordsNew }
                    _showFirst.update { 2 }
                    return@collect
                }

                var monthIndex = 1
                var listIndex = 0
                var first = Instant.ofEpochMilli(list.first().timestamp).atZone(z).minusMonths(1)
                val last = Instant.ofEpochMilli(list.last().timestamp).atZone(z)
                    .plusMonths(if (list.size == 1) 2 else 1)

                while (first < last) {
                    val paged = PagedMonth(
                        monthIndex, Month(
                            first.month.length(false),
                            first.minusDays(first.dayOfMonth.toLong()).dayOfWeek.value,
                            first.month.display(),
                            first.year.toString()
                        ), MonthYear(first.month.value, first.year)
                    )
                    if ((first.month == currentDate.month) && (first.year == currentDate.year)) _showFirst.update { monthIndex }

                    if ((listIndex < list.size) && (Instant.ofEpochMilli(list[listIndex].timestamp)
                            .atZone(z).month.value == first.month.value)
                    ) {
                        recordsNew[paged] =
                            (recordsNew[paged] ?: emptyMap()) + (Instant.ofEpochMilli(
                                list[listIndex].timestamp
                            ).atZone(z).dayOfMonth to list[listIndex])
                        listIndex++
                    } else {
                        recordsNew[paged] = recordsNew[paged] ?: emptyMap()
                        monthIndex++
                        first = first.plusMonths(1)
                    }
                }

                if (_showFirst.value == null) _showFirst.update { 1 }

                _records.update { recordsNew }
            }
        }
    }

    fun modifyRecord(id: Long, type: DefaultMoodType) {
        viewModelScope.launch {
            dao.updateType(id, type)
            fetchMonths()
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
            callback(d.month.value, d.year)
        }
    }

    fun getTimestampForNewRecord(month: PagedMonth, day: Int): Long {
        val now = LocalDateTime.now()
        return LocalDateTime.of(
            month.third.second, month.third.first, day, now.hour, now.minute, now.second
        ).toInstant(ZonedDateTime.now().offset).toEpochMilli()
    }

    fun getGroupedRecords(): Map<Int, List<Pair<Int, String>>> {
        val mp = mutableMapOf<Int, List<Pair<Int, String>>>()
        _records.value.keys.forEach {
            val year = it.third.second
            val month = it.second.third
            val index = it.first
            mp[year] = (mp[year] ?: emptyList()) + (index to month)
        }
        return mp
    }

    fun removeRecord(id: Long) {
        viewModelScope.launch {
            dao.removeRecord(id)
            fetchMonths()
        }
    }
}