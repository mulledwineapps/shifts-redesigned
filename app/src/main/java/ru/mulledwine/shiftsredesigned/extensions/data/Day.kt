package ru.mulledwine.shiftsredesigned.extensions.data

import ru.mulledwine.shiftsredesigned.data.local.entities.Day
import ru.mulledwine.shiftsredesigned.data.local.entities.ScheduleWithShifts
import ru.mulledwine.shiftsredesigned.data.local.models.JobScheduleShiftType
import ru.mulledwine.shiftsredesigned.data.local.models.Month
import ru.mulledwine.shiftsredesigned.data.local.models.StatisticItem
import ru.mulledwine.shiftsredesigned.data.local.models.TimeUnits
import ru.mulledwine.shiftsredesigned.extensions.month
import ru.mulledwine.shiftsredesigned.extensions.year
import ru.mulledwine.shiftsredesigned.utils.Utils

fun Day.toMonth(): Month {
    val calendar = Utils.getCalendarInstance(startTime)
    return Month(calendar.month, calendar.year)
}

fun List<Day>.getStatisticItems(
    schedules: List<ScheduleWithShifts>
): List<StatisticItem> {

    val shifts = this.flatMap { day ->
        day.getShiftItems(schedules).map { it.shiftType }
    }.sortedBy { it.id }

    return shifts.groupBy { it.name }.map {
        StatisticItem(it.key, it.value.size.toString())
    }
}

fun Day.getShiftItems(
    schedules: List<ScheduleWithShifts>
): List<JobScheduleShiftType> {
    return schedules.mapNotNull { getShiftItems(it) }
}

fun Day.getShiftItems(
    schedule: ScheduleWithShifts
): JobScheduleShiftType? {

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
        JobScheduleShiftType(
            jobId = schedule.job.id!!,
            jobName = schedule.job.name,
            scheduleId = schedule.schedule.id!!,
            scheduleDuration = schedule.schedule.getDuration(),
            shiftId = shift.id!!,
            shiftType = type
        )
    }

}