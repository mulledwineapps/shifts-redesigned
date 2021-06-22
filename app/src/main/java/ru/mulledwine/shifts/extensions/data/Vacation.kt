package ru.mulledwine.shifts.extensions.data

import ru.mulledwine.shifts.data.local.entities.ShiftType
import ru.mulledwine.shifts.data.local.entities.Vacation
import ru.mulledwine.shifts.data.local.models.VacationItem
import ru.mulledwine.shifts.data.local.models.VacationParcelable
import ru.mulledwine.shifts.extensions.format
import ru.mulledwine.shifts.extensions.getDaysGenitive
import ru.mulledwine.shifts.extensions.toCalendar
import ru.mulledwine.shifts.utils.Utils

fun Vacation.toVacationItem(shiftType: ShiftType? = null): VacationItem {
    return VacationItem(
        id = id!!,
        duration = getDuration(),
        description = getDescription(shiftType)
    )
}

fun Vacation.toVacationParcelable(shiftType: ShiftType? = null) = VacationParcelable(
    id = id!!,
    start = start,
    finish = finish,
    shiftTypeItem = shiftType?.toShiftTypeItem()
)

fun Vacation.getDuration(): String {
    val start = start.toCalendar().format()
    val finish = finish.toCalendar().format()
    return "$start - $finish"
}

fun Vacation.getDescription(shiftType: ShiftType?): String {
    val firstShift = if (shiftType == null) "" else ", выход со смены - ${shiftType.name}"
    val daysCount = Utils.getDuration(start, finish)
    return "${daysCount.getDaysGenitive()}$firstShift"
}