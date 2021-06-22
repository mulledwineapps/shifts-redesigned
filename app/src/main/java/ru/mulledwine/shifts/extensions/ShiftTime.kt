package ru.mulledwine.shifts.extensions

import ru.mulledwine.shifts.data.local.models.ClockTime
import ru.mulledwine.shifts.data.local.models.TimeUnits

operator fun ClockTime.minus(start: ClockTime): ClockTime {

    var hours = this.hour - start.hour
    var minutes = this.minute - start.minute

    // this: 04:00 start: 22:00
    if (hours < 0) {
        hours += 24
    }

    if (minutes < 0) {
        hours -= 1
        minutes += 60
    }

    return ClockTime(hours, minutes)
}

operator fun ClockTime.plus(other: ClockTime): ClockTime {

    var hours = this.hour + other.hour
    var minutes = this.minute + other.minute

    if (minutes > 60) {
        hours += 1
        minutes -= 60
    }

    return ClockTime(hours, minutes)
}

operator fun ClockTime.compareTo(other: ClockTime): Int {
    return when (true){
        this.hour < other.hour || (this.hour == other.hour && this.minute < other.minute) -> -1
        this.hour > other.hour || (this.hour == other.hour && this.minute > other.minute) -> 1
        else -> 0 // this.hour == other.hour && this.minute == other.minute
    }
}

fun Iterable<ClockTime>.sum(): ClockTime {
    val hours = this.map { it.hour }.sum()
    val minutes = this.map { it.minute }.sum()

    return ClockTime(
        hour = hours + minutes / 60,
        minute = minutes % 60
    )
}

fun ClockTime.toLong(): Long {
    return TimeUnits.HOUR.value * hour + TimeUnits.MINUTE.value * minute
}