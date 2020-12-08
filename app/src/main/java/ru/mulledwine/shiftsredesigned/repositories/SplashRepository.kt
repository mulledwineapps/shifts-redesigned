package ru.mulledwine.shiftsredesigned.repositories

import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.Shift
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType

object SplashRepository {

    private val schedulesDao = db.schedulesDao()
    private val shiftsDao = db.shiftsDao()
    private val shiftTypesDao = db.shiftTypesDao()

    suspend fun insertSchedulesToDb(schedules: List<Schedule>) {
        schedulesDao.insert(schedules)
    }

    suspend fun insertShiftsToDb(shifts: List<Shift>) {
        shiftsDao.insert(shifts)
    }

    suspend fun insertShiftTypesToDb(shiftTypes: List<ShiftType>) {
        shiftTypesDao.insert(shiftTypes)
    }

}