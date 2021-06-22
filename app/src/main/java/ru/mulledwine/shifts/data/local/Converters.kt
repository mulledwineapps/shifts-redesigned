package ru.mulledwine.shifts.data.local

import androidx.room.TypeConverter
import ru.mulledwine.shifts.data.local.models.ClockTime
import ru.mulledwine.shifts.extensions.toClockTime
import ru.mulledwine.shifts.extensions.toLong

class ClockTimeConverter {
    @TypeConverter
    fun longToClockTime(time: Long?): ClockTime? {
        return time?.toClockTime()
    }

    @TypeConverter
    fun clockTimeToLong(clockTime: ClockTime?): Long? = clockTime?.toLong()
}