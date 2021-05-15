package ru.mulledwine.shiftsredesigned.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(): String {
    val pattern = when {
        this.isTheSameDay(Date()) -> "сегодня, dd MMMM в HH:mm"
        this.isTomorrow(Date()) -> "завтра, dd MMMM в HH:mm"
        else -> "dd MMMM в HH:mm"
    }
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.isTheSameDay(other: Date): Boolean {
    val day1 = this.toCalendar()
    val day2 = other.toCalendar()
    return day1.isTheSameDay(day2)
}

fun Date.isTomorrow(other: Date): Boolean {
    val day1 = this.toCalendar()
    val day2 = other.toCalendar()
    day2.add(Calendar.DATE, 1)
    return day1.isTheSameDay(day2)
}

fun Date.toCalendar(): Calendar {
    return this.time.toCalendar()
}