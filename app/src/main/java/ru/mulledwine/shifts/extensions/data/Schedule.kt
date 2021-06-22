package ru.mulledwine.shifts.extensions.data

import ru.mulledwine.shifts.data.local.entities.Schedule
import ru.mulledwine.shifts.data.local.entities.ScheduleWithShifts
import ru.mulledwine.shifts.data.local.models.ScheduleItem
import ru.mulledwine.shifts.data.local.models.ShiftItem
import ru.mulledwine.shifts.extensions.format
import ru.mulledwine.shifts.extensions.toCalendar

interface IDurable {
    val start: Long
    val finish: Long
}

fun IDurable.getDuration(): String {
    return when {
        start == 0L && finish == 0L -> "Продолжительность не задана"
        start == 0L -> {
            val finish = finish.toCalendar().format()
            "Действует до $finish"
        }
        finish == 0L -> {
            val start = start.toCalendar().format()
            "Действует с $start"
        }
        else -> {
            val start = start.toCalendar().format()
            val finish = finish.toCalendar().format()
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

fun ScheduleWithShifts.getShiftsForAlarm(): List<ShiftItem> {
    return shiftsWithTypes.map {
        ShiftItem(
            id = it.value.shift.id!!,
            ordinal = it.value.shift.ordinal,
            typeName = it.value.type.name,
            duration = it.value.type.getDuration(),
            color = it.value.type.color
        )
    }
}