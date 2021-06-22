package ru.mulledwine.shifts.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shifts.data.local.dao.*
import ru.mulledwine.shifts.data.local.entities.Job
import ru.mulledwine.shifts.data.local.entities.Vacation
import ru.mulledwine.shifts.data.local.entities.VacationWithJob
import ru.mulledwine.shifts.data.local.models.VacationParcelable
import ru.mulledwine.shifts.extensions.data.toVacationParcelable
import javax.inject.Inject

class VacationsRepository @Inject constructor(
    private val jobsDao: JobsDao,
    private val vacationsDao: VacationsDao,
    private val shiftsDao: ShiftsDao,
    private val shiftTypesDao: ShiftTypesDao
) {

    fun findJobs(): LiveData<List<Job>> {
        return jobsDao.findJobs()
    }

    fun findVacationsWithJob(): LiveData<List<VacationWithJob>> {
        return vacationsDao.findVacationsWithJob()
    }

    fun findVacations(jobId: Int): LiveData<List<Vacation>> {
        return vacationsDao.findVacationsWithJob(jobId)
    }

    suspend fun getVacations(jobId: Int): List<Vacation> {
        return vacationsDao.getVacationsByJob(jobId)
    }

    suspend fun deleteVacation(id: Int) {
        vacationsDao.deleteVacation(id)
    }

    suspend fun deleteVacations(ids: List<Int>) {
        vacationsDao.deleteVacations(ids)
    }

    suspend fun getVacationParcelable(id: Int): VacationParcelable {
        val vacation = vacationsDao.getVacation(id)
        val shiftType = vacation.firstShiftId?.let {
            val shiftTypeId = shiftsDao.getShiftTypeId(vacation.firstShiftId)
            val shiftType = shiftTypesDao.getShiftType(shiftTypeId)
            shiftType
        }
        return vacation.toVacationParcelable(shiftType)
    }

}