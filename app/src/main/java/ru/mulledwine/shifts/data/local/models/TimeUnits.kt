package ru.mulledwine.shifts.data.local.models

enum class TimeUnits(val value: Long) {
    SECOND(1000L),
    MINUTE(60 * SECOND.value),
    HOUR(60 * MINUTE.value),
    DAY(24 * HOUR.value),
    WEEK(7 * DAY.value);
}