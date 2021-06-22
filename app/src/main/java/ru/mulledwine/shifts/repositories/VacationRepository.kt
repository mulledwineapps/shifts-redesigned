package ru.mulledwine.shifts.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shifts.data.local.dao.JobsDao
import ru.mulledwine.shifts.data.local.dao.ShiftsDao
import ru.mulledwine.shifts.data.local.dao.VacationsDao
import ru.mulledwine.shifts.data.local.entities.Job
import ru.mulledwine.shifts.data.local.entities.ShiftWithType
import ru.mulledwine.shifts.data.local.entities.Vacation
import javax.inject.Inject

class VacationRepository @Inject constructor(
    private val jobsDao: JobsDao,
    private val vacationsDao: VacationsDao,
    private val shiftsDao: ShiftsDao
) {

    fun findJobs(): LiveData<List<Job>> {
        return jobsDao.findJobs()
    }

    fun findShifts(scheduleId: Int): LiveData<List<ShiftWithType>> {
        return shiftsDao.findShifts(scheduleId)
    }

    suspend fun upsertVacation(item: Vacation) {
        if (item.id == null) vacationsDao.insert(item) else vacationsDao.update(item)
    }

}