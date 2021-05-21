package ru.mulledwine.shiftsredesigned.extensions.data

import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTypeListItem

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