package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType

object ShiftTypesRepository {

    private const val TAG = "M_ShiftTypesRepository"

    private val shiftTypesDao = db.shiftTypesDao()

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