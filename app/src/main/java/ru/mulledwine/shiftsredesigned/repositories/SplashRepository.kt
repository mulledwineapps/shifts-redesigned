package ru.mulledwine.shiftsredesigned.repositories

import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.dao.*
import ru.mulledwine.shiftsredesigned.data.local.entities.Job
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.Shift
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import javax.inject.Inject

class SplashRepository @Inject constructor(
    private val jobsDao: JobsDao,
    private val schedulesDao: SchedulesDao,
    private val shiftsDao: ShiftsDao,
    private val shiftTypesDao: ShiftTypesDao
) {
    suspend fun insertJobsToDb(jobs: List<Job>) {
        jobsDao.insert(jobs)
    }

    suspend fun insertSchedulesToDb(schedules: List<Schedule>) {
        schedulesDao.insert(schedules)
    }

    suspend fun insertShiftsToDb(shifts: List<Shift>) {
        shiftsDao.insert(shifts)
    }

    suspend fun insertShiftTypesToDb(shiftTypes: List<ShiftType>) {
        shiftTypesDao.insert(shiftTypes)
    }

}