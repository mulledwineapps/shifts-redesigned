package ru.mulledwine.shifts.viewmodels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mulledwine.shifts.data.local.models.JobItem
import ru.mulledwine.shifts.data.local.models.JobWithScheduleItems
import ru.mulledwine.shifts.data.local.models.ScheduleItem
import ru.mulledwine.shifts.extensions.data.toJobItem
import ru.mulledwine.shifts.extensions.data.toScheduleItem
import ru.mulledwine.shifts.extensions.mutableLiveData
import ru.mulledwine.shifts.repositories.SchedulesRepository
import ru.mulledwine.shifts.ui.schedules.SchedulesFragmentDirections
import ru.mulledwine.shifts.viewmodels.base.IViewModelState
import javax.inject.Inject

fun JobWithScheduleItems.toSchedulesState() = SchedulesState(
    this.jobItem.name,
    this.scheduleItems
)

@HiltViewModel
class SchedulesViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val repository: SchedulesRepository
) : BaseViewModel<SchedulesState>(
    handle,
    handle.get<JobWithScheduleItems>("item")!!.toSchedulesState()
) {

    companion object {
        private const val TAG = "M_SchedulesViewModel"
    }

    private val jobLive = mutableLiveData(handle.get<JobWithScheduleItems>("item")!!.jobItem)

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

    fun handleClickAdd() {
        val action = SchedulesFragmentDirections
            .actionNavSchedulesToNavSchedule(jobLive.value!!)
        navigateWithAction(action)
    }

    fun handleClickEdit(id: Int) {
        launchSafely {
            val schedule = repository.getScheduleWithShiftItems(id)
            val action = SchedulesFragmentDirections
                .actionNavSchedulesToNavSchedule(jobLive.value!!, schedule)
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