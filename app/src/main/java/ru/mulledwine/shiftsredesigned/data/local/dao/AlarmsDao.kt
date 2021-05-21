package ru.mulledwine.shiftsredesigned.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ru.mulledwine.shiftsredesigned.data.local.entities.Alarm
import ru.mulledwine.shiftsredesigned.data.local.entities.AlarmView

@Dao
interface AlarmsDao : BaseDao<Alarm> {

    @Query(
        """
        SELECT * FROM alarms_view
    """
    )
    fun findAlarms(): LiveData<List<AlarmView>>

    @Query(
        """
            SELECT * from alarms_view WHERE alarm_id = :id
        """
    )
    suspend fun getAlarmFull(id: Int): AlarmView

    @Query(
        """
            SELECT * from alarms WHERE id = :id
        """
    )
    suspend fun getAlarm(id: Int): Alarm

    @Query(
        """
            DELETE FROM alarms WHERE id = :id
        """
    )
    suspend fun deleteAlarm(id: Int)

    @Query(
        """
            DELETE FROM alarms WHERE id in (:ids)
        """
    )
    suspend fun deleteAlarms(ids: List<Int>)

    @Query(
        """
        SELECT * FROM alarms_view
        WHERE shift_id = :shiftId
    """
    )
    suspend fun findAlarm(shiftId: Int): AlarmView?

    @Query(
        """
            UPDATE alarms SET is_active = NOT is_active
            WHERE id = :id
        """
    )
    suspend fun toggleAlarm(id: Int)

    @Query(
        """
            SELECT is_active FROM alarms
            WHERE id = :id
        """
    )
    suspend fun isAlarmActive(id: Int): Boolean

    @Query(
        """
            SELECT EXISTS(SELECT 1 FROM alarms WHERE id = :id)
        """
    )
    suspend fun isAlarmExist(id: Int): Boolean

    @Query(
        """
            SELECT shift_id from alarms
            WHERE id = :alarmId
        """
    )
    suspend fun getShiftId(alarmId: Int): Int

    @Query(
        """
            SELECT schedule_id from shifts
            WHERE id = :shiftId
        """
    )
    suspend fun getScheduleId(shiftId: Int): Int

    @Query(
        """
            SELECT is_cyclic from schedules
            WHERE id = :scheduleId
        """
    )
    suspend fun isScheduleCyclic(scheduleId: Int): Boolean

    suspend fun isCyclic(alarmId: Int): Boolean {
        val shiftId = getShiftId(alarmId)
        val scheduleId = getScheduleId(shiftId)
        return isScheduleCyclic(scheduleId)
    }

    @Query(
        """
            SELECT id FROM alarms WHERE shift_id = :shiftId
        """
    )
    suspend fun getAlarmId(shiftId: Int): Int?

    @Query(
        """
            SELECT * FROM ALARMS
        """
    )
    suspend fun getAlarms(): List<Alarm>

}