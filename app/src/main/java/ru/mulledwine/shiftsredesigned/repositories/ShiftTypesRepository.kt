package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.dao.ShiftTypesDao
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
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