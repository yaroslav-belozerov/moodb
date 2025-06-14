package com.yaabelozerov.moodb.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

fun Month.display(tag: String, style: TextStyle = TextStyle.FULL_STANDALONE): String =
    this.getDisplayName(style, Locale(tag)).capit(style == TextStyle.FULL_STANDALONE)

private fun String.capit(apply: Boolean) = if (apply) this.replaceFirstChar { it.uppercase() }
else this


fun Instant.toDate(zoneId: ZoneId): LocalDate = this.atZone(zoneId).toLocalDate()