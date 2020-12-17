package ru.mulledwine.shiftsredesigned.repositories

import androidx.lifecycle.LiveData
import ru.mulledwine.shiftsredesigned.data.local.DbManager.db
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.Vacation
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShort
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleWithShiftItems
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleWithVacationItems
import ru.mulledwine.shiftsredesigned.extensions.data.toScheduleShiftItem
import ru.mulledwine.shiftsredesigned.extensions.data.toVacationItem

object SchedulesRepository {

    private const val TAG = "M_SchedulesRepository"

    private val schedulesDao = db.schedulesDao()

    fun findSchedules(): LiveData<List<Schedule>> {
        return schedulesDao.findSchedules()
    }

    suspend fun deleteSchedule(id: Int) {
        schedulesDao.deleteSchedule(id)
    }

    suspend fun deleteSchedules(ids: List<Int>) {
        schedulesDao.deleteSchedules(ids)
    }

    suspend fun getSchedule(id: Int): ScheduleWithShiftItems {
        val scheduleFull = schedulesDao.getSchedule(id)
        return ScheduleWithShiftItems(
            id = scheduleFull.schedule.id!!,
            name = scheduleFull.schedule.name,
            isCyclic = scheduleFull.schedule.isCyclic,
            start = scheduleFull.schedule.start,
            finish = scheduleFull.schedule.finish,
            shiftItems = scheduleFull.shiftsWithTypes.map {
                it.value.toScheduleShiftItem()
            }
        )
    }

    suspend fun getScheduleShort(id: Int): ScheduleShort {
        return schedulesDao.getScheduleShort(id)
    }

    suspend fun getScheduleWithVacationItems(id: Int?): ScheduleWithVacationItems {
        val scheduleFull = if (id == null) schedulesDao.getScheduleWithVacations()
        else schedulesDao.getScheduleWithVacations(id)

        return ScheduleWithVacationItems(
            schedule = scheduleFull.schedule,
            vacationItems = scheduleFull.vacations
                .sortedWith(compareByDescending<Vacation> { it.start }.thenByDescending { it.finish })
                .map { it.toVacationItem() }
        )
    }

}