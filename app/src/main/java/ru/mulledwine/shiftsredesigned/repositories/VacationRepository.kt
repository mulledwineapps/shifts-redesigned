package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.dao.JobsDao
import ru.mulledwine.shiftsredesigned.data.local.dao.ShiftsDao
import ru.mulledwine.shiftsredesigned.data.local.dao.VacationsDao
import ru.mulledwine.shiftsredesigned.data.local.entities.Job
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftWithType
import ru.mulledwine.shiftsredesigned.data.local.entities.Vacation
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