package ru.mulledwine.shifts.extensions.data

import ru.mulledwine.shifts.data.local.entities.Day
import ru.mulledwine.shifts.data.local.entities.ScheduleWithShifts
import ru.mulledwine.shifts.data.local.entities.Vacation
import ru.mulledwine.shifts.data.local.models.JobScheduleShiftType
import ru.mulledwine.shifts.data.local.models.Month
import ru.mulledwine.shifts.data.local.models.StatisticItem
import ru.mulledwine.shifts.data.local.models.TimeUnits
import ru.mulledwine.shifts.extensions.*

fun Day.toMonth(): Month {
    val calendar = startTime.toCalendar()
    return Month(calendar.month, calendar.year)
}

fun List<Day>.getStatisticItems(
    schedules: List<ScheduleWithShifts>,
    vacations: List<Vacation>
): List<StatisticItem> {

    val shifts = this
        .filterNot { day ->
            vacations.any { v -> day.startTime in v.start..v.finish }
        }
        .flatMap { day ->
            day.getShiftItems(schedules).map { it.shiftType }
        }.sortedBy { it.id }

    val vacationDays = this.count { day ->
        vacations.any { v -> day.startTime in v.start..v.finish }
    }

    if (shifts.isEmpty() && vacationDays == 0) return emptyList()

    val list = mutableListOf<StatisticItem>()

    if (shifts.isNotEmpty()) {
        list.add(StatisticItem.Group("Количество смен"))
        list.addAll(shifts.groupBy { it.name }.map {
            StatisticItem.Element(it.key, it.value.size.toString())
        })
        if (vacationDays != 0) list.add(
            StatisticItem.Element("Дней отпуска", vacationDays.toString())
        )

        val time = shifts.filterNot { it.isDayOff }
            .map { it.finish - it.start }
            .sum()
        val hours = if (time.hour == 0) "" else time.hour.getHoursGenitive()
        val minutes = if (time.minute == 0) "" else time.minute.getMinutesGenitive()
        val total = "$hours $minutes".trim()

        if (total.isNotEmpty()) {
            list.add(StatisticItem.Group("Количество часов"))
            list.add(
                StatisticItem.Element(
                    title = "Рабочее время",
                    value = total
                )
            )
        }
    }
    return list
}

fun Day.getShiftItems(
    schedules: List<ScheduleWithShifts>
): List<JobScheduleShiftType> {
    return schedules.mapNotNull { getShiftItem(it) }
}

private fun Day.getShiftItem(
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

    // времени прошло с начала графика в миллисекундах
    val diff = maxOf(0L, day.startTime - scheduleStartTime)
    // времени прошло с начала графика в днях
    val days = diff / TimeUnits.DAY.value + if (diff % TimeUnits.DAY.value == 0L) 0 else 1

    // нет смены в этот день, если
    // 1) график не цикличный и 2) смен задано меньше, чем продолжительность графика
    if (!schedule.schedule.isCyclic && days >= shifts.size) return null

    val index = (days % shifts.size).toInt()
    val shift = shifts[index].value

    return JobScheduleShiftType(
        jobId = schedule.job.id!!,
        jobName = schedule.job.name,
        scheduleId = schedule.schedule.id!!,
        scheduleTitle = schedule.schedule.getDuration(),
        shiftId = shift.shift.id!!,
        shiftType = shift.type
    )

}