package ru.mulledwine.shiftsredesigned.viewmodels

import android.util.Log
import androidx.lifecycle.*
import kotlinx.android.synthetic.main.item_shift_on_main.*
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.data.local.entities.Day
import ru.mulledwine.shiftsredesigned.data.local.entities.ScheduleWithShifts
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleParcelable
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShiftItem
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftOnMainItem
import ru.mulledwine.shiftsredesigned.data.local.models.TimeUnits
import ru.mulledwine.shiftsredesigned.extensions.data.getDescription
import ru.mulledwine.shiftsredesigned.extensions.data.getDuration
import ru.mulledwine.shiftsredesigned.extensions.mutableLiveData
import ru.mulledwine.shiftsredesigned.extensions.zeroTime
import ru.mulledwine.shiftsredesigned.repositories.MainRepository
import ru.mulledwine.shiftsredesigned.repositories.SchedulesRepository
import ru.mulledwine.shiftsredesigned.ui.main.MainFragmentDirections
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

class MainViewModel(handle: SavedStateHandle) : BaseViewModel<MainState>(handle, MainState()) {

    companion object {
        private const val TAG = "M_MainViewModel"
    }

    private val repository = MainRepository
    private val schedulesRepository = SchedulesRepository

    private val currentDay: MutableLiveData<Day> = mutableLiveData()
    private val schedules = repository.findSchedules()

    private val shiftItems =
        MediatorLiveData<List<ShiftOnMainItem>>().apply {
            val f = f@{
                val currentDay = currentDay.value ?: return@f
                val schedules = schedules.value ?: return@f
                value = getShiftItems(currentDay, schedules)
            }
            value = emptyList()
            addSource(currentDay) { f.invoke() }
            addSource(schedules) { f.invoke() }
        }

    init {
        Log.d(TAG, "init")
        launchSafely {
            updateCurrentDay(repository.getDay(Constants.todayId))
        }
        subscribeOnDataSource(repository.findDays()) { days, state ->
            state.copy(days = days)
        }
        subscribeOnDataSource(shiftItems) { shiftItems, state ->
            state.copy(shiftItems = shiftItems)
        }
        subscribeOnDataSource(currentDay) { currentDay, state ->
            state.copy(currentDayStart = currentDay.start.timeInMillis)
        }
    }

    fun updateCurrentDay(day: Day) {
        currentDay.value = day
    }

    fun handleDeleteSchedule(scheduleId: Int) {
        launchSafely {
            repository.deleteSchedule(scheduleId)
        }
    }

    fun handleEditSchedule(title: String, id: Int) {
        launchSafely {
            val schedule = schedulesRepository.getSchedule(id)
            val action = MainFragmentDirections.actionNavMainToNavSchedule(title, schedule)
            navigateWithAction(action)
        }
    }

    private fun getShiftItems(
        day: Day,
        schedules: List<ScheduleWithShifts>
    ): List<ShiftOnMainItem> {

        return schedules.mapNotNull {

            // all starts should be 0 milliseconds!
            // all finishes should be last millisecond of a day!

            day.start.zeroTime() // TODO why it isn't zero yet here?

            val scheduleStartTime = it.schedule.start
            val scheduleFinishTime = it.schedule.finish
            val dayEnd = day.start.timeInMillis + (TimeUnits.DAY.value - 1L)

            val notStarted = dayEnd < scheduleStartTime
            val isFinished =
                scheduleFinishTime != 0L && day.start.timeInMillis > scheduleFinishTime

            if (notStarted || isFinished) return@mapNotNull null

            val shifts = it.shiftsWithTypes
            if (shifts.isEmpty()) return@mapNotNull null

            val diff = maxOf(0L, day.start.timeInMillis - scheduleStartTime)
            val days = diff / TimeUnits.DAY.value + if (diff % TimeUnits.DAY.value == 0L) 0 else 1
            val index = (days % shifts.size).toInt()

            return@mapNotNull with(shifts[index].value) {
                ShiftOnMainItem(
                    id = shift.id!!,
                    scheduleId = it.schedule.id!!,
                    scheduleName = it.schedule.name,
                    description = type.getDescription(),
                    color = type.color,
                    shiftsCount = shifts.size
                )
            }

        }

    }

}

data class MainState(
    val days: List<Day> = emptyList(),
    val shiftItems: List<ShiftOnMainItem> = emptyList(),
    val shiftTypes: List<ShiftType> = emptyList(),
    val currentDayStart: Long = Constants.today.timeInMillis
) : IViewModelState