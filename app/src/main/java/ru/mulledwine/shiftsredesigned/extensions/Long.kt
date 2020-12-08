package ru.mulledwine.shiftsredesigned.extensions

import ru.mulledwine.shiftsredesigned.utils.Utils
import java.util.*

fun Long.toCalendar(): Calendar {
    val calendar = Utils.getCalendarInstance()
    calendar.timeInMillis = this
    return calendar
}