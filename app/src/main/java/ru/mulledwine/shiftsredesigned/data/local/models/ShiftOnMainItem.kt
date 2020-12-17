package ru.mulledwine.shiftsredesigned.data.local.models

import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.extensions.data.getDescription

data class ShiftOnMainItem(
    val jobId: Int,
    val jobName: String,
    val scheduleId: Int,
    val scheduleDuration: String,
    val shiftId: Int,
    val description: String, // тип смены и её продолжительность
    val color: Int
)

data class JobScheduleShiftType(
    val jobId: Int,
    val jobName: String,
    val scheduleId: Int,
    val scheduleDuration: String,
    val shiftId: Int,
    val shiftType: ShiftType
) {
    fun toShiftOnMainItem() = ShiftOnMainItem(
        jobId = jobId,
        jobName = jobName,
        scheduleId = scheduleId,
        scheduleDuration = scheduleDuration,
        shiftId = shiftId,
        description = shiftType.getDescription(),
        color = shiftType.color
    )
}