package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Day
import ru.mulledwine.shiftsredesigned.data.local.entities.ScheduleWithShifts

object MainRepository {

    private val daysDao = db.daysDao()

    fun findDays(): LiveData<List<Day>> {
        return daysDao.findDays()
    }

    suspend fun getDay(dayId: String): Day {
        return daysDao.getDay(dayId)
    }

}