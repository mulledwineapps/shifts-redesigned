package ru.mulledwine.shifts.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import ru.mulledwine.shifts.data.local.models.JobItem
import ru.mulledwine.shifts.data.local.models.JobWithStatisticItems
import ru.mulledwine.shifts.data.local.models.Month
import ru.mulledwine.shifts.data.local.models.StatisticItem
import ru.mulledwine.shifts.extensions.data.getStatisticItems
import ru.mulledwine.shifts.extensions.data.next
import ru.mulledwine.shifts.extensions.data.previous
import ru.mulledwine.shifts.extensions.data.toJobItem
import ru.mulledwine.shifts.extensions.mutableLiveData
import ru.mulledwine.shifts.repositories.DaysRepository
import ru.mulledwine.shifts.repositories.JobsRepository
import ru.mulledwine.shifts.repositories.SchedulesRepository
import ru.mulledwine.shifts.repositories.VacationsRepository
import ru.mulledwine.shifts.viewmodels.base.IViewModelState

fun JobWithStatisticItems.toStatisticsState() = StatisticsState(
    this.jobItem.name,
    this.month,
    this.statisticItems
)

class StatisticsViewModel @ViewModelInject constructor(
    @Assisted handle: SavedStateHandle,
    private val jobsRepository: JobsRepository,
    private val schedulesRepository: SchedulesRepository,
    private val vacationsRepository: VacationsRepository,
    private val daysRepository: DaysRepository
) :
    BaseViewModel<StatisticsState>(
        handle,
        handle.get<JobWithStatisticItems>("item")!!.toStatisticsState()
    ) {

    private val jobLive = mutableLiveData(handle.get<JobWithStatisticItems>("item")!!.jobItem)
    private val monthLive = mutableLiveData(handle.get<JobWithStatisticItems>("item")!!.month)

    private val schedules = jobLive.switchMap {
        schedulesRepository.findSchedulesWithShifts(it.id)
    }

    private val vacations = jobLive.switchMap {
        vacationsRepository.findVacations(it.id)
    }

    private val days = monthLive.switchMap {
        daysRepository.findDays(it)
    }

    private val statisticItems =
        MediatorLiveData<List<StatisticItem>>().apply {
            val f = f@{
                val days = days.value ?: return@f
                val schedules = schedules.value ?: return@f
                val vacations = vacations.value ?: return@f

                value = days.getStatisticItems(schedules, vacations)
            }

            value = handle.get<JobWithStatisticItems>("item")!!.statisticItems
            addSource(schedules) { f.invoke() }
            addSource(vacations) { f.invoke() }
            addSource(days) { f.invoke() }
        }

    init {
        subscribeOnDataSource(jobLive) { job, state ->
            state.copy(jobName = job.name)
        }
        subscribeOnDataSource(monthLive) { month, state ->
            state.copy(month = month)
        }
        subscribeOnDataSource(statisticItems) { statisticItems, state ->
            state.copy(statisticItems = statisticItems)
        }
    }

    fun observeJobs(owner: LifecycleOwner, onChange: (list: List<JobItem>) -> Unit) {
        jobsRepository.findJobs().map { list -> list.map { it.toJobItem() } }
            .observe(owner, Observer(onChange))
    }

    fun handleUpdateJob(job: JobItem) {
        jobLive.value = job
    }

    fun handleUpdateMonth(month: Month) {
        monthLive.value = month
    }

    fun handleSetPreviousMonth() {
        monthLive.value = monthLive.value!!.previous()
    }

    fun handleSetNextMonth() {
        monthLive.value = monthLive.value!!.next()
    }

}

data class StatisticsState(
    val jobName: String,
    val month: Month,
    val statisticItems: List<StatisticItem>
) : IViewModelState