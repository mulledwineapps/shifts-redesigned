package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.Vacation
import ru.mulledwine.shiftsredesigned.data.local.models.VacationParcelable
import ru.mulledwine.shiftsredesigned.extensions.data.toVacationParcelable

object VacationsRepository {

    private val vacationsDao = db.vacationsDao()
    private val schedulesDao = db.schedulesDao()
    private val shiftsDao = db.shiftsDao()
    private val shiftTypesDao = db.shiftTypesDao()

    fun findSchedules(): LiveData<List<Schedule>> {
        return schedulesDao.findSchedules()
    }

    fun findVacations(scheduleId: Int): LiveData<List<Vacation>> {
        return vacationsDao.findVacations(scheduleId)
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