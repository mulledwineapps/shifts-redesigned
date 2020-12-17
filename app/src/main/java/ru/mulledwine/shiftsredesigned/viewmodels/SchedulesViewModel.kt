package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleItem
import ru.mulledwine.shiftsredesigned.extensions.data.toScheduleItem
import ru.mulledwine.shiftsredesigned.repositories.SchedulesRepository
import ru.mulledwine.shiftsredesigned.ui.schedules.SchedulesFragmentDirections
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class SchedulesViewModel(handle: SavedStateHandle) : BaseViewModel<EmptyState>(handle, EmptyState) {

    companion object {
        private const val TAG = "M_SchedulesViewModel"
    }

    private val repository = SchedulesRepository

    fun observeSchedules(owner: LifecycleOwner, onChange: (list: List<ScheduleItem>) -> Unit) {
        repository.findSchedules().map { list -> list.map { it.toScheduleItem() } }
            .observe(owner, Observer(onChange))
    }

    fun handleEditSchedule(title: String, id: Int) {
        launchSafely {
            val schedule = repository.getSchedule(id)
            val action = SchedulesFragmentDirections
                .actionNavSchedulesToNavSchedule(title, schedule)
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