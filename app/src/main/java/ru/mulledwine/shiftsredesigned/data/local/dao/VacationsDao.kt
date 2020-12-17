package ru.mulledwine.shiftsredesigned.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ru.mulledwine.shiftsredesigned.data.local.entities.Vacation

@Dao
interface VacationsDao : BaseDao<Vacation> {

    @Query(
        """
        SELECT * FROM vacations 
        WHERE schedule_id = :scheduleId
        ORDER BY start DESC, finish DESC
    """
    )
    fun findVacations(scheduleId: Int): LiveData<List<Vacation>>

    @Query(
        """
            SELECT * FROM vacations WHERE id = :id
        """
    )
    suspend fun getVacation(id: Int): Vacation

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