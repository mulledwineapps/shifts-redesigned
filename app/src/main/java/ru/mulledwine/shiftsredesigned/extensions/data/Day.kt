package ru.mulledwine.shiftsredesigned.extensions.data

import ru.mulledwine.shiftsredesigned.data.local.entities.Day
import ru.mulledwine.shiftsredesigned.data.local.entities.ScheduleWithShifts
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftScheduleType
import ru.mulledwine.shiftsredesigned.data.local.models.TimeUnits

fun Day.getShiftItems(
    schedules: List<ScheduleWithShifts>
): List<ShiftScheduleType> {
    return schedules.mapNotNull { getShiftItems(it) }
}

fun Day.getShiftItems(
    schedule: ScheduleWithShifts
): ShiftScheduleType? {

    val day = this

    // all starts should be 0 milliseconds!
    // all finishes should be last millisecond of a day!

    val scheduleStartTime = schedule.schedule.start
    val scheduleFinishTime = schedule.schedule.finish
    val dayEnd = day.startTime + (TimeUnits.DAY.value - 1L)

    val notStarted = dayEnd < scheduleStartTime
    val isFinished =
        scheduleFinishTime != 0L && day.startTime > scheduleFinishTime

    if (notStarted || isFinished) return null

    val shifts = schedule.shiftsWithTypes
    if (shifts.isEmpty()) return null

    val diff = maxOf(0L, day.startTime - scheduleStartTime)
    val days = diff / TimeUnits.DAY.value + if (diff % TimeUnits.DAY.value == 0L) 0 else 1
    val index = (days % shifts.size).toInt()

    return with(shifts[index].value) {
        ShiftScheduleType(
            shiftId = shift.id!!,
            scheduleId = schedule.schedule.id!!,
            scheduleName = schedule.schedule.name,
            shiftType = type
        )
    }

}