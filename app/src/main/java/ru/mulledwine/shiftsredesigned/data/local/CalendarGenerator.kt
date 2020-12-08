package ru.mulledwine.shiftsredesigned.data.local

import android.util.Log
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Day
import ru.mulledwine.shiftsredesigned.extensions.*
import ru.mulledwine.shiftsredesigned.utils.Utils
import java.util.*

object CalendarGenerator {

    private const val TAG = "M_CalendarGenerator"

    private val daysDao = db.daysDao()

    private val pointer = Utils.getCalendarInstance()

    private val firstYear = Constants.today.year - 1
    private val lastYear = Constants.today.year + 1

    suspend fun generate() {
        pointer.setWithZeroTime(firstYear, Calendar.JANUARY, 1)
        var reachCalendarEnd = false

        while (!reachCalendarEnd) {

            reachCalendarEnd = reachCalendarEnd()

            daysDao.insert(
                Day(
                    id = pointer.getDayId(),
                    numberInWeek = pointer.daysFromWeekStart,
                    date = pointer.date,
                    start = pointer
                )
            )

            pointer.moveToNextDay()
        }
    }

    private fun reachCalendarEnd(): Boolean {
        return pointer.year == lastYear &&
                pointer.month == Calendar.DECEMBER &&
                pointer.date == 31
    }

}