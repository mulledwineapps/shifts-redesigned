package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Alarm
import ru.mulledwine.shiftsredesigned.data.local.entities.Job
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShiftItem

object ScheduleRepository {

    private val jobsDao = db.jobsDao()
    private val schedulesDao = db.schedulesDao()
    private val shiftTypesDao = db.shiftTypesDao()
    private val alarmsDao = db.alarmsDao()

    suspend fun upsertSchedule(
        schedule: Schedule,
        shiftsToUpsert: List<ScheduleShiftItem>,
        shiftIdsToDelete: List<Int>
    ) {
        schedulesDao.upsertSchedule(schedule, shiftsToUpsert, shiftIdsToDelete)
    }

    suspend fun getAlarmId(shiftId: Int): Int? {
        return alarmsDao.getAlarmId(shiftId)
    }

    fun findShiftTypes(): LiveData<List<ShiftType>> {
        return shiftTypesDao.findShiftTypes()
    }

    fun findJobs(): LiveData<List<Job>> {
        return jobsDao.findJobs()
    }

}