package ru.mulledwine.shifts.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shifts.data.local.dao.JobsDao
import ru.mulledwine.shifts.data.local.dao.SchedulesDao
import ru.mulledwine.shifts.data.local.entities.Job
import ru.mulledwine.shifts.data.local.entities.Schedule
import ru.mulledwine.shifts.data.local.entities.ScheduleWithShifts
import ru.mulledwine.shifts.data.local.models.ScheduleWithShiftItems
import ru.mulledwine.shifts.extensions.data.toScheduleShiftItem
import javax.inject.Inject

class SchedulesRepository @Inject constructor(
    private val jobsDao: JobsDao,
    private val schedulesDao: SchedulesDao,
) {
    companion object {
        private const val TAG = "M_SchedulesRepository"
    }

    fun findJobs(): LiveData<List<Job>> {
        return jobsDao.findJobs()
    }

    fun findSchedules(jobId: Int): LiveData<List<Schedule>> {
        return schedulesDao.findSchedules(jobId)
    }

    fun findSchedulesWithShifts(): LiveData<List<ScheduleWithShifts>> {
        return schedulesDao.findSchedulesWithShifts()
    }

    fun findSchedulesWithShifts(jobId: Int): LiveData<List<ScheduleWithShifts>> {
        return schedulesDao.findSchedulesWithShifts(jobId)
    }

    suspend fun getSchedulesWithShifts(jobId: Int): List<ScheduleWithShifts> {
        return schedulesDao.getSchedulesWithShifts(jobId)
    }

    suspend fun deleteSchedule(id: Int) {
        schedulesDao.deleteSchedule(id)
    }

    suspend fun deleteSchedules(ids: List<Int>) {
        schedulesDao.deleteSchedules(ids)
    }

    suspend fun getScheduleWithShiftItems(id: Int): ScheduleWithShiftItems {
        val scheduleFull = schedulesDao.getScheduleWithShifts(id)
        return ScheduleWithShiftItems(
            id = scheduleFull.schedule.id!!,
            isCyclic = scheduleFull.schedule.isCyclic,
            start = scheduleFull.schedule.start,
            finish = scheduleFull.schedule.finish,
            shiftItems = scheduleFull.shiftsWithTypes.map {
                it.value.toScheduleShiftItem()
            }
        )
    }

}