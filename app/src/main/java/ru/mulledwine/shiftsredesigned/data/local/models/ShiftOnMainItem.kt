package ru.mulledwine.shiftsredesigned.data.local.models

import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.extensions.data.getDescription

data class ShiftOnMainItem(
    val shiftId: Int,
    val scheduleId: Int,
    val scheduleName: String,
    val description: String, // тип смены и её продолжительность
    val color: Int
)

data class ShiftScheduleType(
    val shiftId: Int,
    val scheduleId: Int,
    val scheduleName: String,
    val shiftType: ShiftType
) {
    fun toShiftOnMainItem() = ShiftOnMainItem(
        shiftId = shiftId,
        scheduleId = scheduleId,
        scheduleName = scheduleName,
        description = shiftType.getDescription(),
        color = shiftType.color
    )
}