package ru.mulledwine.shiftsredesigned.data.local

import androidx.room.TypeConverter
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTime
import ru.mulledwine.shiftsredesigned.utils.Utils
import java.util.*

class CalendarConverter {
    @TypeConverter
    fun timestampToCalendar(timestamp: Long?): Calendar? = timestamp?.let {
        Utils.getCalendarInstance(timestamp)
    }

    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar?): Long? = calendar?.timeInMillis
}

class ShiftTimeConverter {
    @TypeConverter
    fun timeToShiftTime(time: String?): ShiftTime? {
        time ?: return null
        val (hour, minute) = time.split(ShiftTime.separator)
        return ShiftTime(hour.toInt(), minute.toInt())
    }

    @TypeConverter
    fun shiftTimeToName(shiftTime: ShiftTime?): String? = shiftTime?.toString()
}