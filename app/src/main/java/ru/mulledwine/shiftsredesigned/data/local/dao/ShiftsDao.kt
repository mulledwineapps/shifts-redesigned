package ru.mulledwine.shiftsredesigned.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ru.mulledwine.shiftsredesigned.data.local.entities.Shift
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftWithType
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleForAlarm
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftForAlarm

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

    @Query(
        """
            SELECT sh.id, sh.ordinal, st.name, st.start
            FROM shifts sh LEFT JOIN shift_types as st ON st.id = sh.shift_type_id
            WHERE sh.id = :id 
            """
    )
    suspend fun getShiftForAlarm(id: Int): ShiftForAlarm

    @Query(
        """
            SELECT COUNT(*)
            FROM shifts
            WHERE shifts.schedule_id = :scheduleId
        """
    )
    suspend fun getCount(scheduleId: Int): Int

}