package ru.mulledwine.shifts.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shifts.data.local.dao.AlarmsDao
import ru.mulledwine.shifts.data.local.dao.JobsDao
import ru.mulledwine.shifts.data.local.dao.SchedulesDao
import ru.mulledwine.shifts.data.local.dao.ShiftTypesDao
import ru.mulledwine.shifts.data.local.entities.Job
import ru.mulledwine.shifts.data.local.entities.Schedule
import ru.mulledwine.shifts.data.local.entities.ShiftType
import ru.mulledwine.shifts.data.local.models.ScheduleShiftItem
import javax.inject.Inject

class ScheduleRepository @Inject constructor(
    private val jobsDao: JobsDao,
    private val schedulesDao: SchedulesDao,
    private val shiftTypesDao: ShiftTypesDao,
    private val alarmsDao: AlarmsDao
) {

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