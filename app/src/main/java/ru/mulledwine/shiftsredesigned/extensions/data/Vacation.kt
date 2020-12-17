package ru.mulledwine.shiftsredesigned.extensions.data

import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.data.local.entities.Vacation
import ru.mulledwine.shiftsredesigned.data.local.models.VacationItem
import ru.mulledwine.shiftsredesigned.data.local.models.VacationParcelable
import ru.mulledwine.shiftsredesigned.extensions.format
import ru.mulledwine.shiftsredesigned.extensions.getDaysGenitive
import ru.mulledwine.shiftsredesigned.utils.Utils

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
    val start = Utils.getCalendarInstance(start).format()
    val finish = Utils.getCalendarInstance(finish).format()
    return "$start - $finish"
}

fun Vacation.getDescription(shiftType: ShiftType?): String {
    val firstShift = if (shiftType == null) "" else ", выход со смены - ${shiftType.name}"
    val daysCount = Utils.getDuration(start, finish)
    return "${daysCount.getDaysGenitive()}$firstShift"
}