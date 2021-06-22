package ru.mulledwine.shifts.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shifts.data.local.dao.ShiftTypesDao
import ru.mulledwine.shifts.data.local.entities.ShiftType
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