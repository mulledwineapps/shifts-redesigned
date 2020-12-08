package ru.mulledwine.shiftsredesigned.extensions.data

import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTypeItem

fun ShiftType.toShiftTypeItem(): ShiftTypeItem {
    return ShiftTypeItem(
        id = id!!,
        duration = getDuration(),
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