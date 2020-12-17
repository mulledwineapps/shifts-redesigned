package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType

object DaysTuningRepository {

    private val schedulesDao = db.schedulesDao()
    private val shiftTypesDao = db.shiftTypesDao()

    fun findSchedules(): LiveData<List<Schedule>> {
        return schedulesDao.findSchedules()
    }

    fun findShiftTypes(): LiveData<List<ShiftType>> {
        return shiftTypesDao.findShiftTypes()
    }

}