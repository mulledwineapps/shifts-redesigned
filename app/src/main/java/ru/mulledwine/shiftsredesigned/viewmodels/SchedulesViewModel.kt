package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.*
import ru.mulledwine.shiftsredesigned.data.local.models.JobItem
import ru.mulledwine.shiftsredesigned.data.local.models.JobWithScheduleItems
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleItem
import ru.mulledwine.shiftsredesigned.extensions.data.toJobItem
import ru.mulledwine.shiftsredesigned.extensions.data.toScheduleItem
import ru.mulledwine.shiftsredesigned.extensions.mutableLiveData
import ru.mulledwine.shiftsredesigned.repositories.SchedulesRepository
import ru.mulledwine.shiftsredesigned.ui.schedules.SchedulesFragmentDirections
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

class SchedulesViewModel(
    handle: SavedStateHandle,
    param: JobWithScheduleItems
) : BaseViewModel<SchedulesState>(
    handle,
    SchedulesState(param.jobItem.name, param.scheduleItems)
) {

    companion object {
        private const val TAG = "M_SchedulesViewModel"
    }

    private val repository = SchedulesRepository

    private val jobLive = mutableLiveData(param.jobItem)

    private val schedules = jobLive.switchMap {
        repository.findSchedules(it.id)
    }

    init {
        subscribeOnDataSource(jobLive) { job, state ->
            state.copy(jobName = job.name)
        }
        subscribeOnDataSource(schedules) { schedules, state ->
            state.copy(scheduleItems = schedules.mapIndexed { index, it ->
                it.toScheduleItem(index.inc())
            })
        }
    }

    fun observeJobs(owner: LifecycleOwner, onChange: (list: List<JobItem>) -> Unit) {
        repository.findJobs().map { list -> list.map { it.toJobItem() } }
            .observe(owner, Observer(onChange))
    }

    fun handleUpdateJob(job: JobItem) {
        jobLive.value = job
    }

    fun handleClickAdd(title: String) {
        val action = SchedulesFragmentDirections
            .actionNavSchedulesToNavSchedule(title, jobLive.value!!)
        navigateWithAction(action)
    }

    fun handleClickEdit(title: String, id: Int) {
        launchSafely {
            val schedule = repository.getScheduleWithShiftItems(id)
            val action = SchedulesFragmentDirections
                .actionNavSchedulesToNavSchedule(title, jobLive.value!!, schedule)
            navigateWithAction(action)
        }
    }

    fun handleDeleteSchedule(id: Int) {
        launchSafely {
            repository.deleteSchedule(id)
        }
    }

    fun handleDeleteSchedules(ids: List<Int>) {
        launchSafely {
            repository.deleteSchedules(ids)
        }
    }

}

data class SchedulesState(
    val jobName: String,
    val scheduleItems: List<ScheduleItem>
) : IViewModelState