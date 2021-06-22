package ru.mulledwine.shifts.repositories

import ru.mulledwine.shifts.data.local.dao.AlarmsDao
import ru.mulledwine.shifts.data.local.dao.SchedulesDao
import ru.mulledwine.shifts.data.local.dao.ShiftsDao
import ru.mulledwine.shifts.data.local.entities.Alarm
import ru.mulledwine.shifts.data.local.entities.AlarmView
import ru.mulledwine.shifts.data.local.models.ScheduleForAlarm
import ru.mulledwine.shifts.data.local.models.ShiftForAlarm
import javax.inject.Inject

class AlarmRepository @Inject constructor(
    private val alarmsDao: AlarmsDao,
    private val schedulesDao: SchedulesDao,
    private val shiftsDao: ShiftsDao
) {

    suspend fun getScheduleForAlarm(id: Int): ScheduleForAlarm {
        return schedulesDao.getScheduleForAlarm(id)
    }

    suspend fun getShiftForAlarm(id: Int): ShiftForAlarm {
        return shiftsDao.getShiftForAlarm(id)
    }

    suspend fun updateAlarm(alarm: Alarm) {
        alarmsDao.update(alarm)
    }

    suspend fun createAlarm(alarm: Alarm): Long {
        return alarmsDao.insert(alarm)
    }

    suspend fun getAlarm(id: Int): Alarm {
        return alarmsDao.getAlarm(id)
    }

    suspend fun findAlarm(shiftId: Int): AlarmView? {
        return alarmsDao.findAlarm(shiftId)
    }

}