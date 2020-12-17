package ru.mulledwine.shiftsredesigned.extensions.data

import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleItem
import ru.mulledwine.shiftsredesigned.extensions.format
import ru.mulledwine.shiftsredesigned.utils.Utils

fun Schedule.getDuration(): String {
    return when {
        start == 0L && finish == 0L -> "Продолжительность не задана"
        start == 0L -> {
            val finish = Utils.getCalendarInstance(finish).format()
            "Действует до $finish"
        }
        finish == 0L -> {
            val start = Utils.getCalendarInstance(start).format()
            "Действует с $start"
        }
        else -> {
            val start = Utils.getCalendarInstance(start).format()
            val finish = Utils.getCalendarInstance(finish).format()
            "Действует с $start по $finish"
        }
    }
}

fun Schedule.toScheduleItem(ordinal: Int) = ScheduleItem(
    id = id!!,
    ordinal = ordinal,
    duration = getDuration(),
    isCyclic = isCyclic
)