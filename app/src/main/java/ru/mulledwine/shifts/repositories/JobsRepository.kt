package ru.mulledwine.shifts.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shifts.data.local.dao.JobsDao
import ru.mulledwine.shifts.data.local.entities.Job
import ru.mulledwine.shifts.data.local.entities.Schedule
import ru.mulledwine.shifts.data.local.entities.Vacation
import ru.mulledwine.shifts.data.local.models.JobWithScheduleItems
import ru.mulledwine.shifts.data.local.models.JobWithVacationItems
import ru.mulledwine.shifts.extensions.data.toJobItem
import ru.mulledwine.shifts.extensions.data.toScheduleItem
import ru.mulledwine.shifts.extensions.data.toVacationItem
import javax.inject.Inject

class JobsRepository @Inject constructor(
    private val jobsDao: JobsDao
) {

    fun findJobs(): LiveData<List<Job>> {
        return jobsDao.findJobs()
    }

    suspend fun getJob(id: Int): Job {
        return jobsDao.getJob(id)
    }

    suspend fun getJob(): Job? {
        return jobsDao.getJob()
    }

    suspend fun insertJob(job: Job): Long {
        return jobsDao.insert(job)
    }

    suspend fun updateJob(job: Job) {
        jobsDao.update(job)
    }

    suspend fun deleteJob(id: Int) {
        jobsDao.deleteJob(id)
    }

    suspend fun deleteJobs(ids: List<Int>) {
        jobsDao.deleteJobs(ids)
    }

    suspend fun jobsCount(): Int {
        return jobsDao.jobsCount()
    }

    suspend fun getJobWithScheduleItems(id: Int?): JobWithScheduleItems {
        val jobFull = if (id == null) jobsDao.getJobWithSchedules()
        else jobsDao.getJobWithSchedules(id)

        return JobWithScheduleItems(
            jobItem = jobFull.job.toJobItem(),
            scheduleItems = jobFull.schedules
                .sortedWith(compareByDescending<Schedule> { it.start }.thenByDescending { it.finish })
                .mapIndexed { index, it -> it.toScheduleItem(index.inc()) }
        )
    }

    suspend fun getJobWithVacationItems(id: Int?): JobWithVacationItems {
        val jobFull = if (id == null) jobsDao.getJobWithVacations()
        else jobsDao.getJobWithVacations(id)

        return JobWithVacationItems(
            jobItem = jobFull.job.toJobItem(),
            vacationItems = jobFull.vacations
                .sortedWith(compareByDescending<Vacation> { it.start }.thenByDescending { it.finish })
                .map { it.toVacationItem() }
        )
    }

}