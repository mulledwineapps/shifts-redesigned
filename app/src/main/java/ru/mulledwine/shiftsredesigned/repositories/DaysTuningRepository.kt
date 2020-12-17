package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Job
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType

object DaysTuningRepository {

    private val jobsDao = db.jobsDao()
    private val shiftTypesDao = db.shiftTypesDao()

    fun findJobs(): LiveData<List<Job>> {
        return jobsDao.findJobs()
    }

    fun findShiftTypes(): LiveData<List<ShiftType>> {
        return shiftTypesDao.findShiftTypes()
    }

}