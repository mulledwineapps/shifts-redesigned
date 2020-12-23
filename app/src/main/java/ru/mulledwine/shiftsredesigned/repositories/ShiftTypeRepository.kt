package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType

object ShiftTypeRepository {

    private val shiftTypesDao = db.shiftTypesDao()

    fun findShiftTypes(): LiveData<List<ShiftType>> {
        return shiftTypesDao.findShiftTypes()
    }

    suspend fun updateShiftType(item: ShiftType) {
        shiftTypesDao.update(item)
    }

    suspend fun createShiftType(item: ShiftType) {
        shiftTypesDao.insert(item)
    }

}