package ru.mulledwine.shifts.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shifts.data.local.dao.ShiftTypesDao
import ru.mulledwine.shifts.data.local.entities.ShiftType
import javax.inject.Inject

class ShiftTypesRepository @Inject constructor(
    private val shiftTypesDao: ShiftTypesDao
) {
    companion object {
        private const val TAG = "M_ShiftTypesRepository"
    }

    fun findShiftTypes(): LiveData<List<ShiftType>> {
        return shiftTypesDao.findShiftTypes()
    }

    suspend fun getShiftType(id: Int): ShiftType {
        return shiftTypesDao.getShiftType(id)
    }

    suspend fun deleteShiftType(id: Int) {
        shiftTypesDao.deleteShiftType(id)
    }

}