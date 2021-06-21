package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.dao.AlarmsDao
import ru.mulledwine.shiftsredesigned.data.local.dao.ShiftsDao
import ru.mulledwine.shiftsredesigned.data.local.entities.Alarm
import ru.mulledwine.shiftsredesigned.data.local.entities.AlarmView
import javax.inject.Inject

class AlarmsRepository @Inject constructor(
    private val alarmsDao: AlarmsDao,
    private val shiftsDao: ShiftsDao
) {

    fun findAlarms(): LiveData<List<AlarmView>> {
        return alarmsDao.findAlarms()
    }

    suspend fun getAlarmFull(id: Int): AlarmView {
        return alarmsDao.getAlarmFull(id)
    }

    suspend fun getAlarm(id: Int): Alarm {
        return alarmsDao.getAlarm(id)
    }

    suspend fun deleteAlarm(id: Int) {
        alarmsDao.deleteAlarm(id)
    }

    suspend fun deleteAlarms(ids: List<Int>) {
        alarmsDao.deleteAlarms(ids)
    }

    suspend fun toggleAlarm(id: Int) {
        alarmsDao.toggleAlarm(id)
    }

    suspend fun isCyclic(alarmId: Int): Boolean {
        return alarmsDao.isCyclic(alarmId)
    }

    suspend fun isExist(id: Int): Boolean {
        return alarmsDao.isAlarmExist(id)
    }

    suspend fun isActive(id: Int): Boolean {
        return alarmsDao.isAlarmActive(id)
    }

    suspend fun getShiftsCount(scheduleId: Int): Int {
        return shiftsDao.getCount(scheduleId)
    }

}