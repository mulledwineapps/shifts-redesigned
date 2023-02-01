package ru.mulledwine.shifts.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.entities.Schedule
import ru.mulledwine.shifts.data.local.models.JobItem
import ru.mulledwine.shifts.data.local.models.ScheduleShiftItem
import ru.mulledwine.shifts.data.local.models.ShiftTypeListItem
import ru.mulledwine.shifts.extensions.data.toJobItem
import ru.mulledwine.shifts.extensions.data.toShiftTypeItem
import ru.mulledwine.shifts.repositories.ScheduleRepository
import ru.mulledwine.shifts.viewmodels.base.EmptyState
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val repository: ScheduleRepository
) : BaseViewModel<EmptyState>(handle, EmptyState) {

    companion object {
        private const val TAG = "M_ScheduleViewModel"
    }

    fun handleClickSave(
        schedule: Schedule,
        shiftsToUpsert: List<ScheduleShiftItem>,
        shiftIdsToDelete: List<Int>,
        cancelAlarmsCallback: (ids: List<Int>) -> Unit
    ) {
        launchSafely {
            repository.upsertSchedule(schedule, shiftsToUpsert, shiftIdsToDelete)
            val shiftsWereChanged = shiftsToUpsert.filter { it.shiftId != it.originalShiftTypeId }
            val alarmsToCancel = shiftsWereChanged.mapNotNull {
                repository.getAlarmId(it.shiftId)
            }
            if (alarmsToCancel.isNotEmpty()) {
                cancelAlarmsCallback(alarmsToCancel)
                val message = "Будильники для изменённых смен были отключены"
                notify(Notify.ActionMessage(message, Snackbar.LENGTH_INDEFINITE, "ОК") {})
            }
            navigateUp()
            // TODO был случай, когда изменение смены не отразилось в календаре для сегодняшнего дня
            // не удалось повторить
        }
    }

    fun observeShiftTypes(
        owner: LifecycleOwner,
        onChange: (list: List<ShiftTypeListItem>) -> Unit
    ) {
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