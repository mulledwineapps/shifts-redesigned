package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType

object ShiftTypeRepository {

    private const val TAG = "M_ShiftTypeRepository"

    private val shiftTypesDao = db.shiftTypesDao()

    suspend fun updateShiftType(item: ShiftType) {
        shiftTypesDao.update(item)
    }

    suspend fun createShiftType(item: ShiftType) {
        shiftTypesDao.insert(item)
    }

}