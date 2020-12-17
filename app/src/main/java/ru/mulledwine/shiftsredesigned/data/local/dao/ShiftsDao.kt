package ru.mulledwine.shiftsredesigned.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ru.mulledwine.shiftsredesigned.data.local.entities.Shift
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftWithType

@Dao
interface ShiftsDao : BaseDao<Shift> {

    @Transaction
    @Query(
        """
            SELECT * FROM shifts WHERE schedule_id = :scheduleId ORDER BY ordinal
        """
    )
    fun findShifts(scheduleId: Int): LiveData<List<ShiftWithType>>

    @Query(
        """
            SELECT shift_type_id FROM shifts WHERE id = :id
        """
    )
    suspend fun getShiftTypeId(id: Int): Int

}