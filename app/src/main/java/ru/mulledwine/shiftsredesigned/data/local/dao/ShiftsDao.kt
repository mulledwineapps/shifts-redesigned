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
    fun getShifts(scheduleId: Int): LiveData<List<ShiftWithType>>

}