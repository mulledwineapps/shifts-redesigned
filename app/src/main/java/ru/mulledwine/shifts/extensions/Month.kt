package ru.mulledwine.shifts.extensions.data

import ru.mulledwine.shifts.data.local.models.Month

fun Month.previous(): Month {
    return if (number == 0) copy(number = 11, year = year - 1)
    else copy(number = number - 1)
}

fun Month.next(): Month {
    return if (number == 11) copy(number = 0, year = year + 1)
    else copy(number = number + 1)
}