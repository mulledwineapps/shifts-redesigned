package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Job
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.ScheduleWithShifts
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleWithShiftItems
import ru.mulledwine.shiftsredesigned.extensions.data.toScheduleShiftItem

object SchedulesRepository {

    private const val TAG = "M_SchedulesRepository"

    private val jobsDao = db.jobsDao()
    private val schedulesDao = db.schedulesDao()

    fun findJobs(): LiveData<List<Job>> {
        return jobsDao.findJobs()
    }

    fun findSchedules(): LiveData<List<ScheduleWithShifts>> {
        return schedulesDao.findSchedulesWithShifts()
    }

    fun findSchedules(jobId: Int): LiveData<List<Schedule>> {
        return schedulesDao.findSchedules(jobId)
    }

    suspend fun deleteSchedule(id: Int) {
        schedulesDao.deleteSchedule(id)
    }

    suspend fun deleteSchedules(ids: List<Int>) {
        schedulesDao.deleteSchedules(ids)
    }

    suspend fun getSchedule(id: Int): ScheduleWithShiftItems {
        val scheduleFull = schedulesDao.getSchedule(id)
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