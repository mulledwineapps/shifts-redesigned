package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShiftItem

object ScheduleRepository {

    private const val TAG = "M_ScheduleRepository"

    private val schedulesDao = db.schedulesDao()
    private val shiftTypesDao = db.shiftTypesDao()

    suspend fun updateSchedule(
        schedule: Schedule,
        shiftsToUpsert: List<ScheduleShiftItem>,
        shiftIdsToDelete: List<Int>
    ) {
        schedulesDao.updateSchedule(schedule, shiftsToUpsert, shiftIdsToDelete)
    }

    fun findShiftTypes(): LiveData<List<ShiftType>> {
        return shiftTypesDao.findShiftTypes()
    }

}