package ru.mulledwine.shiftsredesigned.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.android.synthetic.main.item_shift_on_main.*
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.entities.AlarmParcelable
import ru.mulledwine.shiftsredesigned.data.local.entities.Day
import ru.mulledwine.shiftsredesigned.data.local.entities.Job
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.data.local.models.*
import ru.mulledwine.shiftsredesigned.extensions.data.*
import ru.mulledwine.shiftsredesigned.extensions.mutableLiveData
import ru.mulledwine.shiftsredesigned.extensions.toAlarmParcelable
import ru.mulledwine.shiftsredesigned.repositories.*
import ru.mulledwine.shiftsredesigned.ui.main.MainFragmentDirections
import ru.mulledwine.shiftsredesigned.ui.vacations.VacationsFragmentDirections
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

class MainViewModel(handle: SavedStateHandle) : BaseViewModel<MainState>(handle, MainState()) {

    companion object {
        private const val TAG = "M_MainViewModel"
    }

    private val daysRepository = DaysRepository
    private val jobsRepository = JobsRepository
    private val schedulesRepository = SchedulesRepository
    private val alarmRepository = AlarmRepository
    private val vacationsRepository = VacationsRepository

    private val currentDay: MutableLiveData<Day> = mutableLiveData()
    private val schedules = schedulesRepository.findSchedulesWithShifts()
    private val vacations = vacationsRepository.findVacationsWithJob()

    private val shiftItems =
        MediatorLiveData<List<ShiftOnMainItem>>().apply {
            val f = f@{
                val currentDay = currentDay.value ?: return@f
                val schedules = schedules.value ?: return@f
                val vacations = vacations.value ?: return@f

                val currentVacations = vacations.filter {
                    currentDay.startTime in it.content.start..it.content.finish
                }

                val result = currentDay.getShiftItems(schedules).map {
                    val vacation = currentVacations.firstOrNull { v -> v.job.id == it.jobId }
                    it.toShiftOnMainItem(vacation?.content?.id, vacation?.content?.getDuration())
                }

                // отпуска по тем работам, у которых нет текущего графика
                val notShownVacations =
                    currentVacations.filterNot { result.any { r -> r.jobId == it.job.id } }

                val vacationsResult = notShownVacations.map {
                    ShiftOnMainItem(
                        jobId = it.job.id!!,
                        jobName = it.job.name,
                        vacationId = it.content.id,
                        vacationTitle = it.content.getDuration()
                    )
                }

                value = result + vacationsResult
            }
            value = emptyList()
            addSource(currentDay) { f.invoke() }
            addSource(schedules) { f.invoke() }
            addSource(vacations) { f.invoke() }
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
            val job = jobsRepository.getJob()?.toJobItem()

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

    fun handleClickEdit(
        titleEditSchedule: String,
        titleEditVacation: String,
        item: ShiftOnMainItem
    ) {
        launchSafely {
            val job = jobsRepository.getJob(item.jobId).toJobItem()

            if (item.scheduleId != null) {
                val schedule = schedulesRepository.getScheduleWithShiftItems(item.scheduleId)
                val action = MainFragmentDirections
                    .actionNavMainToNavSchedule(titleEditSchedule, job, schedule)
                navigateWithAction(action)
            } else if (item.vacationId != null) {
                val vacation = vacationsRepository.getVacationParcelable(item.vacationId)
                val action = MainFragmentDirections
                    .actionNavMainToNavVacation(titleEditVacation, job, vacation)
                navigateWithAction(action)
            }
        }
    }

    fun handleAddJobThenSchedule(name: String, context: Context) {
        launchSafely {
            val job = Job(name = name)
            val id = jobsRepository.insertJob(job)

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

    fun handleDeleteVacation(vacationId: Int) {
        launchSafely {
            vacationsRepository.deleteVacation(vacationId)
        }
    }

    fun handleDeleteVacations(ids: List<Int>) {
        launchSafely {
            vacationsRepository.deleteVacations(ids)
        }
    }

    fun navigateToJobs() {
        val action = MainFragmentDirections.actionNavMainToNavJobs()
        navigateWithAction(action)
    }

    fun navigateToSchedules(jobId: Int?) {
        launchSafely {
            val item = jobsRepository.getJobWithScheduleItems(jobId)
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
            val item = jobsRepository.getJobWithVacationItems(jobId)
            val action = MainFragmentDirections.actionNavMainToNavVacations(item)
            navigateWithAction(action)
        }
    }

    fun navigateToTuning(jobId: Int) {
        launchSafely {
            val job = jobsRepository.getJob(jobId).toJobItem()
            val action = MainFragmentDirections.actionNavMainToNavDaysTuning(job)
            navigateWithAction(action)
        }
    }

    // TODO использовать шиммер на экране статистики, чтобы не готовить данные заранее?
    fun navigateToStatistics(jobId: Int) {

        launchSafely {

            val job = jobsRepository.getJob(jobId).toJobItem()
            val month = currentDay.value!!.toMonth()
            val days = daysRepository.getDays(month)
            val schedules = schedulesRepository.getSchedulesWithShifts(jobId)
            val vacations = vacationsRepository.getVacations(jobId)
            val statisticItems = days.getStatisticItems(schedules, vacations)

            val item = JobWithStatisticItems(job, month, statisticItems)
            val action = MainFragmentDirections.actionNavMainToNavStatistics(item)
            navigateWithAction(action)
        }
    }

    fun navigateToAlarm(
        titleAdd: String,
        titleEdit: String,
        item: ShiftOnMainItem
    ) {
        launchSafely {
            if (item.shiftId == null || item.scheduleId == null) return@launchSafely

            var alarm = alarmRepository.findAlarm(item.shiftId)?.toAlarmParcelable()
            val title = if (alarm == null) titleAdd else titleEdit
            if (alarm == null) {
                val job = jobsRepository.getJob(item.jobId).toJobItem()
                val schedule = alarmRepository.getScheduleForAlarm(item.scheduleId)
                val shift = alarmRepository.getShiftForAlarm(item.shiftId)
                alarm = AlarmParcelable(
                    alarm = null,
                    job = job,
                    schedule = schedule,
                    shift = shift
                )
            }
            val action = MainFragmentDirections.actionNavMainToNavAlarm(title, alarm)
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