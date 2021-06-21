package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.dao.DaysDao
import ru.mulledwine.shiftsredesigned.data.local.entities.Day
import ru.mulledwine.shiftsredesigned.data.local.models.Month
import javax.inject.Inject

class DaysRepository @Inject constructor(
    private val daysDao: DaysDao
) {

    fun findDays(): LiveData<List<Day>> {
        return daysDao.findDays()
    }

    fun findDays(month: Month): LiveData<List<Day>> {
        return daysDao.findDays(month.number, month.year)
    }

    suspend fun getDay(dayId: String): Day {
        return daysDao.getDay(dayId)
    }

    suspend fun getDays(month: Month): List<Day> {
        return daysDao.getDays(month.number, month.year)
    }

}