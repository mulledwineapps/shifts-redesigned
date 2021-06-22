package ru.mulledwine.shifts.extensions.data

import ru.mulledwine.shifts.data.local.entities.ShiftWithType
import ru.mulledwine.shifts.data.local.models.ScheduleShiftItem

fun ShiftWithType.toScheduleShiftItem(): ScheduleShiftItem {
    return ScheduleShiftItem(
        shiftId = shift.id!!,
        shiftTypeId = type.id!!,
        ordinal = shift.ordinal,
        title = type.getDuration(),
        typeName = type.name,
        color = type.color,
        isNewItem = false,
        originalShiftTypeId = type.id
    )
}