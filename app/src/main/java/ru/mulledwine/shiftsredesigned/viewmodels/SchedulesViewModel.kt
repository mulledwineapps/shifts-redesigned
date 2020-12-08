package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleItem
import ru.mulledwine.shiftsredesigned.extensions.data.getDuration
import ru.mulledwine.shiftsredesigned.repositories.SchedulesRepository
import ru.mulledwine.shiftsredesigned.ui.schedules.SchedulesFragmentDirections
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class SchedulesViewModel(handle: SavedStateHandle) : BaseViewModel<EmptyState>(handle, EmptyState) {

    companion object {
        private const val TAG = "M_SchedulesViewModel"
    }

    private val repository = SchedulesRepository

    fun observeSchedules(owner: LifecycleOwner, onChange: (list: List<ScheduleItem>) -> Unit) {
        repository.findSchedules().map { list ->
            list.map {
                ScheduleItem(
                    id = it.id!!,
                    name = it.name,
                    duration = it.getDuration(),
                )
            }
        }.observe(owner, Observer(onChange))
    }

    fun handleEditSchedule(title: String, id: Int) {
        launchSafely {
            val schedule = repository.getSchedule(id)
            val action = SchedulesFragmentDirections
                .actionNavSchedulesToNavSchedule(title, schedule)
            navigateWithAction(action)
        }
    }

    fun handleNavigateAddSchedule(title: String) {
        val action = SchedulesFragmentDirections.actionNavSchedulesToNavSchedule(title)
        navigateWithAction(action)
    }

    fun handleDeleteSchedule(scheduleId: Int) {
        launchSafely {
            repository.deleteSchedule(scheduleId)
        }
    }

}