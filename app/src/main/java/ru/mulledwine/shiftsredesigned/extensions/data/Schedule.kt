package ru.mulledwine.shiftsredesigned.extensions.data

import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleItem
import ru.mulledwine.shiftsredesigned.extensions.formatAndCapitalize
import ru.mulledwine.shiftsredesigned.utils.Utils

fun Schedule.getDuration(): String {
    return when {
        start == 0L && finish == 0L -> "Продолжительность не задана"
        start == 0L -> {
            val finish = Utils.getCalendarInstance(finish).formatAndCapitalize()
            "Последний день - $finish"
        }
        finish == 0L -> {
            val start = Utils.getCalendarInstance(start).formatAndCapitalize()
            "Первый день - $start"
        }
        else -> {
            val start = Utils.getCalendarInstance(start).formatAndCapitalize()
            val finish = Utils.getCalendarInstance(finish).formatAndCapitalize()
            "$start - $finish"
        }
    }
}

fun Schedule.toScheduleItem() = ScheduleItem(
    id = id!!,
    name = name,
    duration = getDuration(),
    isCyclic = isCyclic
)