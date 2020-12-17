package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftWithType
import ru.mulledwine.shiftsredesigned.data.local.entities.Vacation

object VacationRepository {

    private val vacationsDao = db.vacationsDao()
    private val schedulesDao = db.schedulesDao()
    private val shiftsDao = db.shiftsDao()

    fun findSchedules(): LiveData<List<Schedule>> {
        return schedulesDao.findSchedules()
    }

    fun findShifts(scheduleId: Int): LiveData<List<ShiftWithType>> {
        return shiftsDao.findShifts(scheduleId)
    }

    suspend fun upsertVacation(item: Vacation) {
        if (item.id == null) vacationsDao.insert(item) else vacationsDao.update(item)
    }

}