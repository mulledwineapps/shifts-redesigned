package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.models.JobItem
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShiftItem
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTypeItem
import ru.mulledwine.shiftsredesigned.extensions.data.toJobItem
import ru.mulledwine.shiftsredesigned.extensions.data.toShiftTypeItem
import ru.mulledwine.shiftsredesigned.repositories.ScheduleRepository
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class ScheduleViewModel(handle: SavedStateHandle) : BaseViewModel<EmptyState>(handle, EmptyState) {

    companion object {
        private const val TAG = "M_ScheduleViewModel"
    }

    private val repository = ScheduleRepository

    fun handleClickSave(
        schedule: Schedule,
        shiftsToUpsert: List<ScheduleShiftItem>,
        shiftIdsToDelete: List<Int>
    ) {
        launchSafely {
            repository.upsertSchedule(schedule, shiftsToUpsert, shiftIdsToDelete)
            navigateUp()
        }
    }

    fun observeShiftTypes(owner: LifecycleOwner, onChange: (list: List<ShiftTypeItem>) -> Unit) {
        repository.findShiftTypes().map { list ->
            list.map { it.toShiftTypeItem() }
        }.observe(owner, Observer(onChange))
    }

    fun observeJobs(owner: LifecycleOwner, onChange: (list: List<JobItem>) -> Unit) {
        repository.findJobs().map { list -> list.map { it.toJobItem() } }
            .observe(owner, Observer(onChange))
    }

    fun handleInputError(errorType: InputErrors) {
        val message = Notify.TextMessage(
            message = errorType.message,
            anchorViewId = R.id.btn_add
        )
        notify(message)
    }

}

sealed class InputErrors {

    abstract val message: String

    // TODO move it to job
    object NameIsBlank : InputErrors() {
        override val message: String = "Название графика не должно быть пустым"
    }

    object IllegalScheduleDuration : InputErrors() {
        override val message: String = "Последний день графика не должен быть раньше первого"
    }

    object NoShifts : InputErrors() {
        override val message: String = "График должен содержать хотя бы одну смену"
    }

    data class TooManyShifts(
        val scheduleDuration: Int
    ) : InputErrors() {
        override val message: String =
            "Количество смен не должно превышать заданное для графика количество дней ($scheduleDuration)"
    }

}