package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleParcelable
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShiftItem
import ru.mulledwine.shiftsredesigned.extensions.data.getDuration

object SchedulesRepository {

    private const val TAG = "M_MainRepository"

    private val schedulesDao = db.schedulesDao()

    fun findSchedules(): LiveData<List<Schedule>> {
        return schedulesDao.findSchedules()
    }

    suspend fun deleteSchedule(id: Int) {
        schedulesDao.deleteSchedule(id)
    }

    suspend fun getSchedule(id: Int): ScheduleParcelable {
        val scheduleFull = schedulesDao.getSchedule(id)
        return ScheduleParcelable(
            id = scheduleFull.schedule.id!!,
            name = scheduleFull.schedule.name,
            start = scheduleFull.schedule.start,
            finish = scheduleFull.schedule.finish,
            shiftItems = scheduleFull.shiftsWithTypes.map {
                val shiftFull = it.value
                ScheduleShiftItem(
                    shiftId = shiftFull.shift.id!!,
                    shiftTypeId = shiftFull.type.id!!,
                    ordinal = shiftFull.shift.ordinal,
                    duration = shiftFull.type.getDuration(),
                    typeName = shiftFull.type.name,
                    color = shiftFull.type.color,
                    isNewItem = false
                )
            }
        )
    }

}