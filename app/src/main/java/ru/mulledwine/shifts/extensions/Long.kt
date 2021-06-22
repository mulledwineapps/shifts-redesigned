package ru.mulledwine.shifts.extensions

import ru.mulledwine.shifts.data.local.models.ClockTime
import ru.mulledwine.shifts.data.local.models.TimeUnits
import ru.mulledwine.shifts.utils.Utils
import java.util.*
import kotlin.math.absoluteValue

fun Long.toCalendar(): Calendar {
    return Utils.getCalendarInstance(this)
}

fun Long.toDate(): Date {
    return Utils.getCalendarInstance(this).time
}

// лишние миллисекунды считаются минутой
fun Long.toClockTime(): ClockTime {
    val hours = this / TimeUnits.HOUR.value
    val hoursLong = hours * TimeUnits.HOUR.value
    var minutes = (this - hoursLong) / TimeUnits.MINUTE.value
    val minutesLong = minutes * TimeUnits.MINUTE.value
    val milliseconds = (this - hoursLong - minutesLong)
    if (milliseconds > 0) minutes++
    return ClockTime(hours.toInt(), minutes.toInt())
}

fun Long.toTimeDuration(): String {
    val days = this / TimeUnits.DAY.value
    val daysLong = days * TimeUnits.DAY.value
    val hours = (this - daysLong) / TimeUnits.HOUR.value
    val hoursLong = hours * TimeUnits.HOUR.value
    var minutes = (this - daysLong - hoursLong) / TimeUnits.MINUTE.value
    val minutesLong = minutes * TimeUnits.MINUTE.value
    val milliseconds = (this - daysLong - hoursLong - minutesLong)
    if (milliseconds > 0) minutes++

    return buildString {
        if (days != 0L) append("$days дн. ")
        if (hours != 0L) append("$hours ч. ")
        if (minutes != 0L) append("$minutes мин. ")
    }.trim()
}

fun Long.whenHappen(): String {
    val now = Utils.getTime()
    val delta = this - now
    val duration = delta.absoluteValue.toTimeDuration()

    return when {
        delta in 0..TimeUnits.MINUTE.value -> "менее чем через минуту"
        delta < 0 -> "$duration назад"
        else -> "через $duration"
    }
}

// TODO протестировать будильники в doze mode