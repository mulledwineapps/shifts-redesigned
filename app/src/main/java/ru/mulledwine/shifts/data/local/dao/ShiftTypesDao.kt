package ru.mulledwine.shifts.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ru.mulledwine.shifts.data.local.entities.ShiftType

@Dao
interface ShiftTypesDao : BaseDao<ShiftType> {

    @Query(
        """
        SELECT * FROM shift_types
        """
    )
    fun findShiftTypes(): LiveData<List<ShiftType>>

    @Query(
        """
            SELECT * FROM shift_types WHERE id = :id 
        """
    )
    suspend fun getShiftType(id: Int): ShiftType

    @Query(
        """
            DELETE FROM shift_types WHERE id = :id
        """
    )
    suspend fun deleteShiftType(id: Int)

}