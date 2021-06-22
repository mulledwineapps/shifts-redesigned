package ru.mulledwine.shifts.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ru.mulledwine.shifts.data.local.entities.Day

@Dao
interface DaysDao : BaseDao<Day> {

    @Query(
        """
            SELECT * FROM days
        """
    )
    fun findDays(): LiveData<List<Day>>

    @Query(
        """
            SELECT * FROM days
            WHERE month = :month and year = :year
        """
    )
    fun findDays(month: Int, year: Int): LiveData<List<Day>>

    @Query(
        """
            SELECT * FROM days WHERE id = :dayId
        """
    )
    suspend fun getDay(dayId: String): Day

    @Query(
        """
            SELECT * FROM days
            WHERE month = :month and year = :year
        """
    )
    suspend fun getDays(month: Int, year: Int): List<Day>

}