package ru.mulledwine.shiftsredesigned.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.android.synthetic.main.item_shift_on_main.*
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.entities.Day
import ru.mulledwine.shiftsredesigned.data.local.entities.Job
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.data.local.models.*
import ru.mulledwine.shiftsredesigned.extensions.data.getShiftItems
import ru.mulledwine.shiftsredesigned.extensions.data.getStatisticItems
import ru.mulledwine.shiftsredesigned.extensions.data.toJobItem
import ru.mulledwine.shiftsredesigned.extensions.data.toMonth
import ru.mulledwine.shiftsredesigned.extensions.mutableLiveData
import ru.mulledwine.shiftsredesigned.repositories.DaysRepository
import ru.mulledwine.shiftsredesigned.repositories.JobsRepository
import ru.mulledwine.shiftsredesigned.repositories.SchedulesRepository
import ru.mulledwine.shiftsredesigned.ui.main.MainFragmentDirections
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

class MainViewModel(handle: SavedStateHandle) : BaseViewModel<MainState>(handle, MainState()) {

    companion object {
        private const val TAG = "M_MainViewModel"
    }

    private val daysRepository = DaysRepository
    private val jobRepository = JobsRepository
    private val schedulesRepository = SchedulesRepository

    private val currentDay: MutableLiveData<Day> = mutableLiveData()
    private val schedules = schedulesRepository.findSchedulesWithShifts()

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
            updateCurrentDay(daysRepository.getDay(Constants.todayId))
        }
        subscribeOnDataSource(daysRepository.findDays()) { days, state ->
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

    fun handleClickAdd(context: Context) {
        launchSafely {
            val job = jobRepository.getJob()?.toJobItem()

            val action = if (job == null) {
                MainFragmentDirections.actionNavMainToDialogAddJob()
            } else {
                // TODO try to define the title in fragment, like in job dialog
                val title = context.getString(R.string.label_add_schedule)
                MainFragmentDirections.actionNavMainToNavSchedule(title, job)
            }

            navigateWithAction(action)
        }
    }

    fun handleClickEdit(title: String, item: ShiftOnMainItem) {
        launchSafely {
            val job = jobRepository.getJob(item.jobId).toJobItem()
            val schedule = schedulesRepository.getSchedule(item.scheduleId)
            val action = MainFragmentDirections.actionNavMainToNavSchedule(title, job, schedule)
            navigateWithAction(action)
        }
    }

    fun handleAddJobThenSchedule(name: String, context: Context) {
        launchSafely {
            val job = Job(name = name)
            val id = jobRepository.insertJob(job)

            Log.d(TAG, "handleAddJobThenSchedule: id $id")

            val jobItem = JobItem(id.toInt(), name)
            val action = MainFragmentDirections
                .actionNavMainToNavSchedule(context.getString(R.string.label_add_schedule), jobItem)
            navigateWithAction(action)
        }
    }

    fun handleDeleteSchedule(scheduleId: Int) {
        launchSafely {
            schedulesRepository.deleteSchedule(scheduleId)
        }
    }

    fun handleDeleteSchedules(ids: List<Int>) {
        launchSafely {
            schedulesRepository.deleteSchedules(ids)
        }
    }

    fun navigateToJobs() {
        val action = MainFragmentDirections.actionNavMainToNavJobs()
        navigateWithAction(action)
    }

    fun navigateToSchedules(jobId: Int?) {
        launchSafely {
            val item = jobRepository.getJobWithScheduleItems(jobId)
            val action = MainFragmentDirections.actionNavMainToNavSchedules(item)
            navigateWithAction(action)
        }
    }

    fun navigateToShiftTypes() {
        val action = MainFragmentDirections.actionNavMainToNavShiftTypes()
        navigateWithAction(action)
    }

    fun navigateToVacations(jobId: Int?) {
        launchSafely {
            val item = jobRepository.getJobWithVacationItems(jobId)
            val action = MainFragmentDirections.actionNavMainToNavVacations(item)
            navigateWithAction(action)
        }
    }

    fun navigateToTuning(jobId: Int) {
        launchSafely {
            val job = jobRepository.getJob(jobId).toJobItem()
            val action = MainFragmentDirections.actionNavMainToNavDaysTuning(job)
            navigateWithAction(action)
        }
    }

    // TODO использовать шиммер на экране статистики, чтобы не готовить данные заранее?
    fun navigateToStatistics(jobId: Int) {

        launchSafely {

            val job = jobRepository.getJob(jobId).toJobItem()
            val month = currentDay.value!!.toMonth()
            val days = daysRepository.getDays(month)
            val schedules = schedulesRepository.getSchedulesWithShifts(jobId)
            val statisticItems = days.getStatisticItems(schedules)

            val item = JobWithStatisticItems(job, month, statisticItems)
            val action = MainFragmentDirections.actionNavMainToNavStatistics(item)
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