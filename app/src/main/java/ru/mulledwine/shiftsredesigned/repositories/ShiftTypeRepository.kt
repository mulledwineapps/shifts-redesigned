package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Alarm
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType

object ShiftTypeRepository {

    private val shiftTypesDao = db.shiftTypesDao()
    private val alarmsDao = db.alarmsDao()

    fun findShiftTypes(): LiveData<List<ShiftType>> {
        return shiftTypesDao.findShiftTypes()
    }

    suspend fun updateShiftType(item: ShiftType) {
        shiftTypesDao.update(item)
    }

    suspend fun createShiftType(item: ShiftType): Long {
        return shiftTypesDao.insert(item)
    }

    suspend fun updateAlarm(alarm: Alarm) {
        alarmsDao.update(alarm)
    }

    suspend fun createAlarm(alarm: Alarm): Long {
        return alarmsDao.insert(alarm)
    }

}