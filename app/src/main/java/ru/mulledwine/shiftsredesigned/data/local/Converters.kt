package ru.mulledwine.shiftsredesigned.data.local

import androidx.room.TypeConverter
import ru.mulledwine.shiftsredesigned.data.local.models.ClockTime
import ru.mulledwine.shiftsredesigned.extensions.toClockTime
import ru.mulledwine.shiftsredesigned.extensions.toLong

class ClockTimeConverter {
    @TypeConverter
    fun longToClockTime(time: Long?): ClockTime? {
        return time?.toClockTime()
    }

    @TypeConverter
    fun clockTimeToLong(clockTime: ClockTime?): Long? = clockTime?.toLong()
}