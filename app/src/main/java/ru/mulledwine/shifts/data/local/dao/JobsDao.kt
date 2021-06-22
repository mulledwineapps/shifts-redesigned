package ru.mulledwine.shifts.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ru.mulledwine.shifts.data.local.entities.Job
import ru.mulledwine.shifts.data.local.entities.JobWithSchedules
import ru.mulledwine.shifts.data.local.entities.JobWithVacations

@Dao
interface JobsDao : BaseDao<Job> {

    @Query(
        """
        SELECT * FROM jobs
    """
    )
    fun findJobs(): LiveData<List<Job>>

    @Query(
        """
            SELECT COUNT(*) FROM jobs
        """
    )
    suspend fun jobsCount(): Int

    @Transaction
    @Query(
        """
            SELECT * FROM jobs
        """
    )
    fun findJobsWithVacations(): LiveData<List<JobWithVacations>>

    @Query(
        """
            SELECT * from jobs WHERE id = :id
        """
    )
    suspend fun getJob(id: Int): Job

    @Query(
        """
            SELECT * from jobs LIMIT 1
        """
    )
    suspend fun getJob(): Job?

    @Query(
        """
            DELETE FROM jobs WHERE id = :id
        """
    )
    suspend fun deleteJob(id: Int)

    @Query(
        """
            DELETE FROM jobs WHERE id in (:ids)
        """
    )
    suspend fun deleteJobs(ids: List<Int>)

    @Transaction
    @Query(
        """
            SELECT * FROM jobs WHERE id = :id 
        """
    )
    suspend fun getJobWithSchedules(id: Int): JobWithSchedules

    @Transaction
    @Query(
        """
            SELECT * FROM jobs LIMIT 1            
        """
    )
    suspend fun getJobWithSchedules(): JobWithSchedules

    @Transaction
    @Query(
        """
            SELECT * FROM jobs WHERE id = :id 
        """
    )
    suspend fun getJobWithVacations(id: Int): JobWithVacations

    @Transaction
    @Query(
        """
            SELECT * FROM jobs LIMIT 1
        """
    )
    suspend fun getJobWithVacations(): JobWithVacations

}