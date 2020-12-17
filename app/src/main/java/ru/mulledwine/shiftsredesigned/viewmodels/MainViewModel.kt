package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.*
import kotlinx.android.synthetic.main.item_shift_on_main.*
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.data.local.entities.Day
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftOnMainItem
import ru.mulledwine.shiftsredesigned.extensions.data.getShiftItems
import ru.mulledwine.shiftsredesigned.extensions.mutableLiveData
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
                value = currentDay.getShiftItems(schedules).map {
                    it.toShiftOnMainItem()
                }
            }
            value = emptyList()
            addSource(currentDay) { f.invoke() }
            addSource(schedules) { f.invoke() }
        }

    init {

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
            state.copy(currentDayStart = currentDay.startTime)
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

    fun handleDeleteSchedules(ids: List<Int>) {
        launchSafely {
            repository.deleteSchedules(ids)
        }
    }

    fun navigateToEditSchedule(title: String, id: Int) {
        launchSafely {
            val schedule = schedulesRepository.getSchedule(id)
            val action = MainFragmentDirections.actionNavMainToNavSchedule(title, schedule)
            navigateWithAction(action)
        }
    }

    fun navigateToTuning(id: Int) {
        launchSafely {
            val schedule = schedulesRepository.getScheduleShort(id)
            val action = MainFragmentDirections.actionNavMainToNavDaysTuning(schedule)
            navigateWithAction(action)
        }
    }

    fun navigateToVacations(scheduleId: Int?) {
        launchSafely {
            val item = schedulesRepository.getScheduleWithVacationItems(scheduleId)
            val action = MainFragmentDirections.actionNavMainToNavVacations(item)
            navigateWithAction(action)
        }
    }

}

data class MainState(
    val days: List<Day> = emptyList(),
    val shiftItems: List<ShiftOnMainItem> = emptyList(),
    val shiftTypes: List<ShiftType> = emptyList(),
    val currentDayStart: Long = Constants.today.timeInMillis
) : IViewModelState