package ru.mulledwine.shifts.data.local

import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.data.local.dao.DaysDao
import ru.mulledwine.shifts.data.local.entities.Day
import ru.mulledwine.shifts.extensions.*
import ru.mulledwine.shifts.utils.Utils
import java.util.*
import javax.inject.Inject

class CalendarGenerator @Inject constructor(
    private val daysDao: DaysDao
) {

    companion object {
        private const val TAG = "M_CalendarGenerator"
    }

    private val pointer = Utils.getCalendarInstance()

    suspend fun generate() {
        pointer.setWithZeroTime(Constants.firstYear, Calendar.JANUARY, 1)
        var reachCalendarEnd = false

        while (!reachCalendarEnd) {

            reachCalendarEnd = reachCalendarEnd()

            daysDao.insert(
                Day(
                    id = pointer.getDayId(),
                    date = pointer.date,
                    month = pointer.month,
                    year = pointer.year,
                    numberInWeek = pointer.daysFromWeekStart,
                    startTime = pointer.timeInMillis
                )
            )

            pointer.moveToNextDay()
        }
    }

    private fun reachCalendarEnd(): Boolean {
        return pointer.year == Constants.lastYear &&
                pointer.month == Calendar.DECEMBER &&
                pointer.date == 31
    }

}