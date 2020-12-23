package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.*
import ru.mulledwine.shiftsredesigned.data.local.models.JobItem
import ru.mulledwine.shiftsredesigned.data.local.models.JobWithStatisticItems
import ru.mulledwine.shiftsredesigned.data.local.models.Month
import ru.mulledwine.shiftsredesigned.data.local.models.StatisticItem
import ru.mulledwine.shiftsredesigned.extensions.data.getStatisticItems
import ru.mulledwine.shiftsredesigned.extensions.data.next
import ru.mulledwine.shiftsredesigned.extensions.data.previous
import ru.mulledwine.shiftsredesigned.extensions.data.toJobItem
import ru.mulledwine.shiftsredesigned.extensions.mutableLiveData
import ru.mulledwine.shiftsredesigned.repositories.DaysRepository
import ru.mulledwine.shiftsredesigned.repositories.JobsRepository
import ru.mulledwine.shiftsredesigned.repositories.SchedulesRepository
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

class StatisticsViewModel(
    handle: SavedStateHandle,
    param: JobWithStatisticItems
) :
    BaseViewModel<StatisticsState>(
        handle,
        StatisticsState(param.jobItem.name, param.month, param.statisticItems)
    ) {

    private val jobsRepository = JobsRepository
    private val schedulesRepository = SchedulesRepository
    private val daysRepository = DaysRepository

    private val jobLive = mutableLiveData(param.jobItem)
    private val monthLive = mutableLiveData(param.month)

    private val schedules = jobLive.switchMap {
        schedulesRepository.findSchedulesWithShifts(it.id)
    }

    private val days = monthLive.switchMap {
        daysRepository.findDays(it)
    }

    private val statisticItems =
        MediatorLiveData<List<StatisticItem>>().apply {
            val f = f@{
                val days = days.value ?: return@f
                val schedules = schedules.value ?: return@f
                value = days.getStatisticItems(schedules)
            }

            value = param.statisticItems
            addSource(schedules) { f.invoke() }
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