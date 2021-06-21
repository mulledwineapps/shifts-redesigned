package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.dao.AlarmsDao
import ru.mulledwine.shiftsredesigned.data.local.dao.ShiftTypesDao
import ru.mulledwine.shiftsredesigned.data.local.entities.Alarm
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import javax.inject.Inject

class ShiftTypeRepository @Inject constructor(
    private val shiftTypesDao: ShiftTypesDao
) {
    fun findShiftTypes(): LiveData<List<ShiftType>> {
        return shiftTypesDao.findShiftTypes()
    }

    suspend fun updateShiftType(item: ShiftType) {
        shiftTypesDao.update(item)
    }

    suspend fun createShiftType(item: ShiftType): Long {
        return shiftTypesDao.insert(item)
    }

}