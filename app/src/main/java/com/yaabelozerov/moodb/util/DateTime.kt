package com.yaabelozerov.moodb.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

fun Month.display(style: TextStyle = TextStyle.FULL_STANDALONE): String =
    this.getDisplayName(style, Locale.getDefault()).capit(style == TextStyle.FULL_STANDALONE)

private fun String.capit(apply: Boolean) = if (apply) this.replaceFirstChar { it.uppercase() }
else this


fun Calendar.calendarLocalDate(zoneId: ZoneId): LocalDateTime =
    this.time.toInstant().atZone(zoneId).toLocalDateTime()

fun Instant.toDate(zoneId: ZoneId): LocalDate = this.atZone(zoneId).toLocalDate()