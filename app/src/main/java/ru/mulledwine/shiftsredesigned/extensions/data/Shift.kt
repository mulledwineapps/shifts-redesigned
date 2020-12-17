package ru.mulledwine.shiftsredesigned.extensions.data

import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftWithType
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShiftItem

fun ShiftWithType.toScheduleShiftItem(): ScheduleShiftItem {
    return ScheduleShiftItem(
        shiftId = shift.id!!,
        shiftTypeId = type.id!!,
        ordinal = shift.ordinal,
        duration = type.getDuration(),
        typeName = type.name,
        color = type.color,
        isNewItem = false
    )
}