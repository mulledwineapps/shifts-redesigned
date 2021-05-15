package ru.mulledwine.shiftsredesigned.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ru.mulledwine.shiftsredesigned.data.local.entities.Vacation
import ru.mulledwine.shiftsredesigned.data.local.entities.VacationWithJob

@Dao
interface VacationsDao : BaseDao<Vacation> {

    @Query(
        """
        SELECT * FROM vacations
    """
    )
    fun findVacationsWithJob(): LiveData<List<VacationWithJob>>

    @Query(
        """
        SELECT * FROM vacations 
        WHERE job_id = :jobId
        ORDER BY start DESC, finish DESC
    """
    )
    fun findVacationsWithJob(jobId: Int): LiveData<List<Vacation>>

    @Query(
        """
            SELECT * FROM vacations WHERE id = :id
        """
    )
    suspend fun getVacation(id: Int): Vacation

    @Query(
        """
            SELECT * FROM vacations WHERE job_id = :jobId
        """
    )
    suspend fun getVacationsByJob(jobId: Int): List<Vacation>

    @Query(
        """
            DELETE FROM vacations WHERE id = :id
        """
    )
    suspend fun deleteVacation(id: Int)

    @Query(
        """
            DELETE FROM vacations WHERE id in (:ids)
        """
    )
    suspend fun deleteVacations(ids: List<Int>)

}