package ru.mulledwine.shiftsredesigned.extensions

import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTime

operator fun ShiftTime.minus(start: ShiftTime): ShiftTime {

    // TODO смена через полночь

    var hours = this.hour - start.hour
    var minutes = this.minute - start.minute

    if (minutes < 0) {
        hours -= 1
        minutes += 60
    }

    return ShiftTime(hours, minutes)
}

fun Iterable<ShiftTime>.sum(): ShiftTime {
    val hours = this.map { it.hour }.sum()
    val minutes = this.map { it.minute }.sum()

    return ShiftTime(
        hour = hours + minutes / 60,
        minute = minutes % 60
    )
}