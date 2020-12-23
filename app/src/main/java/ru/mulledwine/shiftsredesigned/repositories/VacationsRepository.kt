package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Job
import ru.mulledwine.shiftsredesigned.data.local.entities.Vacation
import ru.mulledwine.shiftsredesigned.data.local.models.VacationParcelable
import ru.mulledwine.shiftsredesigned.extensions.data.toVacationParcelable

object VacationsRepository {

    private val jobsDao = db.jobsDao()
    private val vacationsDao = db.vacationsDao()
    private val shiftsDao = db.shiftsDao()
    private val shiftTypesDao = db.shiftTypesDao()

    fun findJobs(): LiveData<List<Job>> {
        return jobsDao.findJobs()
    }

    fun findVacations(jobId: Int): LiveData<List<Vacation>> {
        return vacationsDao.findVacations(jobId)
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