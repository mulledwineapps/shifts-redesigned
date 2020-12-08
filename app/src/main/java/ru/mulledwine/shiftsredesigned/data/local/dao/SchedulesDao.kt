package ru.mulledwine.shiftsredesigned.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.ScheduleWithShifts
import ru.mulledwine.shiftsredesigned.data.local.entities.Shift
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShiftItem

@Dao
interface SchedulesDao : BaseDao<Schedule> {

    @Transaction
    @Query(
        """
        SELECT * FROM schedules
    """
    )
    fun findSchedulesWithShifts(): LiveData<List<ScheduleWithShifts>>

    @Query(
        """
        SELECT * FROM schedules ORDER BY start DESC, finish DESC
    """
    )
    fun findSchedules(): LiveData<List<Schedule>>

    @Query(
        """
            SELECT * FROM schedules WHERE id = :id 
        """
    )
    fun findSchedule(id: Int): LiveData<Schedule?>

    @Query(
        """
            DELETE FROM schedules WHERE id = :id
        """
    )
    suspend fun deleteSchedule(id: Int)

    @Transaction
    @Query(
        """
            SELECT * FROM schedules WHERE id = :id 
        """
    )
    suspend fun getSchedule(id: Int): ScheduleWithShifts

    @Insert
    suspend fun insertShift(shift: Shift)

    @Update
    suspend fun updateShift(shift: Shift)

    @Query(
        """
            DELETE FROM shifts WHERE id = :id
        """
    )
    suspend fun deleteShift(id: Int)

    @Transaction
    suspend fun updateSchedule(
        schedule: Schedule,
        shiftsToUpsert: List<ScheduleShiftItem>,
        shiftIdsToDelete: List<Int>
    ) {
        val scheduleId = if (schedule.id == null) insert(schedule).toInt() else {
            update(schedule)
            schedule.id
        }

        shiftsToUpsert.forEach {
            when {
                it.isNewItem -> {
                    val shift = Shift(
                        scheduleId = scheduleId,
                        shiftTypeId = it.shiftTypeId,
                        ordinal = it.ordinal
                    )
                    insertShift(shift)
                }
                else -> {
                    val shift = Shift(
                        id = it.shiftId,
                        scheduleId = scheduleId,
                        shiftTypeId = it.shiftTypeId,
                        ordinal = it.ordinal
                    )
                    updateShift(shift)
                }
            }
        }

        shiftIdsToDelete.forEach {
            deleteShift(it)
        }

    }

}