package ru.mulledwine.shiftsredesigned.repositories

import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Alarm
import ru.mulledwine.shiftsredesigned.data.local.entities.AlarmView
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleForAlarm
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftForAlarm

object AlarmRepository {

    private val alarmsDao = db.alarmsDao()
    private val schedulesDao = db.schedulesDao()
    private val shiftsDao = db.shiftsDao()

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