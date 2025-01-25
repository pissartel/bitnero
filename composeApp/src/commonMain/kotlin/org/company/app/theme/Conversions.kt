package org.company.app.theme

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Long.convertMillisToYearString(): String {
    val localDateTime = this.toLocalDateTime()
    return localDateTime.year.toString()
}

fun Long.convertMillisToMonthString(): String {
    val localDateTime = this.toLocalDateTime()
    return localDateTime.month.toString().take(3)
}

fun Long.convertMillisToReadableDate(format: String): String {
    val localDateTime = this.toLocalDateTime()
    return format
        .replace("yyyy", localDateTime.year.toString().takeLast(2))
        .replace("MM", localDateTime.month.toString().padStart(2, '0').take(3))
        .replace("dd", localDateTime.dayOfMonth.toString().padStart(2, '0'))
        .replace("HH", localDateTime.hour.toString().padStart(2, '0'))
        .replace("mm", localDateTime.minute.toString().padStart(2, '0'))
        .replace("ss", localDateTime.second.toString().padStart(2, '0'))
}

fun Long.toLocalDateTime(): LocalDateTime {
    val startInstant = Instant.fromEpochMilliseconds(this)
    return startInstant.toLocalDateTime(TimeZone.currentSystemDefault())
}

fun generateRangeWithDivisions(start: Long, end: Long, divisions: Int): List<Long> {
    require(divisions > 0) { "Divisions must be greater than 0" }

    val range = end - start
    val step = range / divisions
    val result = mutableListOf<Long>()

    for (i in 0..divisions) {
        val currentValue = start + (i * step)
        result.add(currentValue)
    }

    return result
}

const val ONE_HOUR_IN_MILLIS = 1000 * 60 * 60L