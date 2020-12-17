package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Job
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.Vacation
import ru.mulledwine.shiftsredesigned.data.local.models.JobWithScheduleItems
import ru.mulledwine.shiftsredesigned.data.local.models.JobWithVacationItems
import ru.mulledwine.shiftsredesigned.extensions.data.toJobItem
import ru.mulledwine.shiftsredesigned.extensions.data.toScheduleItem
import ru.mulledwine.shiftsredesigned.extensions.data.toVacationItem

object JobsRepository {

    private val jobsDao = db.jobsDao()

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