package ru.mulledwine.shiftsredesigned.data.local

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

    val firstYear = Constants.today.year - 2
    val lastYear = Constants.today.year + 2

    suspend fun generate() {
        pointer.setWithZeroTime(firstYear, Calendar.JANUARY, 1)
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
        return pointer.year == lastYear &&
                pointer.month == Calendar.DECEMBER &&
                pointer.date == 31
    }

}