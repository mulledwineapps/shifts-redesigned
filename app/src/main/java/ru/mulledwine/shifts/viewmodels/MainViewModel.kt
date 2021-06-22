package ru.mulledwine.shifts.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.PrefManager
import ru.mulledwine.shifts.data.local.entities.AlarmFullParcelable
import ru.mulledwine.shifts.data.local.entities.Day
import ru.mulledwine.shifts.data.local.entities.Job
import ru.mulledwine.shifts.data.local.entities.ShiftType
import ru.mulledwine.shifts.data.local.models.*
import ru.mulledwine.shifts.extensions.data.*
import ru.mulledwine.shifts.extensions.mutableLiveData
import ru.mulledwine.shifts.extensions.toAlarmParcelable
import ru.mulledwine.shifts.repositories.*
import ru.mulledwine.shifts.ui.main.HintItemRes
import ru.mulledwine.shifts.ui.main.MainFragmentDirections
import ru.mulledwine.shifts.ui.main.MainItem
import ru.mulledwine.shifts.viewmodels.base.IViewModelState

class MainViewModel @ViewModelInject constructor(
    @Assisted handle: SavedStateHandle,
    private val daysRepository: DaysRepository,
    private val jobsRepository: JobsRepository,
    private val schedulesRepository: SchedulesRepository,
    private val alarmRepository: AlarmRepository,
    private val vacationsRepository: VacationsRepository,
    private val preferences: PrefManager
) : BaseViewModel<MainState>(handle, MainState()) {

    companion object {
        private const val TAG = "M_MainViewModel"
    }

    private val currentDay: MutableLiveData<Day> = mutableLiveData()
    private val schedules = schedulesRepository.findSchedulesWithShifts()
    private val vacations = vacationsRepository.findVacationsWithJob()

    private val shiftItems =
        MediatorLiveData<List<MainItem.Element>>().apply {
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
                    MainItem.Element(
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
        subscribeOnDataSource(preferences.shownHintsLive) { shownHints, state ->
            state.copy(shownHints = shownHints.split(PrefManager.delimiter))
        }
    }

    fun updateCurrentDay(day: Day) {
        currentDay.value = day
    }

    fun handleClickAdd() {
        launchSafely {
            val job = jobsRepository.getJob()?.toJobItem()

            val action = if (job == null) {
                MainFragmentDirections.actionNavMainToDialogAddJob(nextDestination = R.id.nav_schedule)
            } else {
                MainFragmentDirections.actionNavMainToNavSchedule(job)
            }

            navigateWithAction(action)
        }
    }

    fun handleClickEdit(item: MainItem.Element) {
        launchSafely {
            val job = jobsRepository.getJob(item.jobId).toJobItem()

            if (item.scheduleId != null) {
                val schedule = schedulesRepository.getScheduleWithShiftItems(item.scheduleId)
                val action = MainFragmentDirections.actionNavMainToNavSchedule(job, schedule)
                navigateWithAction(action)
            } else if (item.vacationId != null) {
                val vacation = vacationsRepository.getVacationParcelable(item.vacationId)
                val action = MainFragmentDirections.actionNavMainToNavVacation(job, vacation)
                navigateWithAction(action)
            }
        }
    }

    fun handleAddJobThenSchedule(name: String) {
        launchSafely {
            val job = Job(name = name)
            val id = jobsRepository.insertJob(job)

            val jobItem = JobItem(id.toInt(), name)
            val action = MainFragmentDirections.actionNavMainToNavSchedule(jobItem)
            navigateWithAction(action)
        }
    }

    fun handleAddJobThenGoToSchedules(name: String) {
        launchSafely {
            val job = Job(name = name)
            val id = jobsRepository.insertJob(job).toInt()

            val jobItem = jobsRepository.getJobWithScheduleItems(id)
            val action = MainFragmentDirections.actionNavMainToNavSchedules(jobItem)
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

    fun navigateToSchedules() {
        launchSafely {
            if (jobsRepository.jobsCount() == 0) {
                val action = MainFragmentDirections.actionNavMainToDialogAddJob(
                    nextDestination = R.id.nav_schedules
                )
                navigateWithAction(action)
            } else {
                val item = jobsRepository.getJobWithScheduleItems(null)
                val action = MainFragmentDirections.actionNavMainToNavSchedules(item)
                navigateWithAction(action)
            }
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
        item: MainItem.Element
    ) {
        launchSafely {
            if (item.shiftId == null || item.scheduleId == null) return@launchSafely

            var alarm = alarmRepository.findAlarm(item.shiftId)?.toAlarmParcelable()
            val title = if (alarm == null) titleAdd else titleEdit
            if (alarm == null) {
                val job = jobsRepository.getJob(item.jobId).toJobItem()
                val schedule = alarmRepository.getScheduleForAlarm(item.scheduleId)
                val shift = alarmRepository.getShiftForAlarm(item.shiftId)
                alarm = AlarmFullParcelable(
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

    fun getShownHints(): List<String> {
        return preferences.getShownHints()
    }

    fun addHintToShown(id: String) {
        preferences.addHintToShown(id)
    }

}

data class MainState(
    val days: List<Day> = emptyList(),
    val shiftItems: List<MainItem.Element> = emptyList(),
    val shiftTypes: List<ShiftType> = emptyList(),
    val shownHints: List<String> = emptyList(),
    val currentDayStart: Long = Constants.today.timeInMillis
) : IViewModelState