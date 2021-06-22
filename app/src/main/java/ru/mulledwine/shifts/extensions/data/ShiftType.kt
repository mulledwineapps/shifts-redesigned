package ru.mulledwine.shifts.extensions.data

import ru.mulledwine.shifts.data.local.entities.ShiftType
import ru.mulledwine.shifts.data.local.models.ShiftTypeListItem

fun ShiftType.toShiftTypeItem(): ShiftTypeListItem {
    return ShiftTypeListItem(
        id = id!!,
        title = getDuration(),
        name = name,
        color = color
    )
}

fun ShiftType.getDescription(): String {
    val duration = if (isFullDay) "- Весь день" else "$start - $finish"
    return "$name $duration"
}

fun ShiftType.getDuration(): String {
    return if (isFullDay) "Весь день" else "$start - $finish"
}