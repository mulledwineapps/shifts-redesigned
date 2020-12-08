package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Day
import ru.mulledwine.shiftsredesigned.data.local.entities.ScheduleWithShifts

object MainRepository {
    private const val TAG = "M_MainRepository"

    private val daysDao = db.daysDao()
    private val schedulesDao = db.schedulesDao()

    fun findDays(): LiveData<List<Day>> {
        return daysDao.findDays()
    }

    fun findSchedules(): LiveData<List<ScheduleWithShifts>> {
        return schedulesDao.findSchedulesWithShifts()
    }

    suspend fun getDay(dayId: String): Day {
        return daysDao.getDay(dayId)
    }

    suspend fun deleteSchedule(id: Int) {
        schedulesDao.deleteSchedule(id)
    }

}