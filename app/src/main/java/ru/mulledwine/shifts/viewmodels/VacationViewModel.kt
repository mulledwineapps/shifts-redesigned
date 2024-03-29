package ru.mulledwine.shifts.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mulledwine.shifts.data.local.entities.ShiftWithType
import ru.mulledwine.shifts.data.local.entities.Vacation
import ru.mulledwine.shifts.repositories.VacationRepository
import ru.mulledwine.shifts.viewmodels.base.EmptyState
import javax.inject.Inject

@HiltViewModel
class VacationViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val repository: VacationRepository
) : BaseViewModel<EmptyState>(handle, EmptyState) {

    fun observePattern(
        scheduleId: Int,
        owner: LifecycleOwner,
        onChange: (list: List<ShiftWithType>) -> Unit
    ) {
        repository.findShifts(scheduleId).observe(owner, Observer(onChange))
    }

    fun handleClickSave(item: Vacation) {

        if (item.start == 0L) {
            val notify = Notify.ErrorMessage("Не задан первый день отпуска")
            notify(notify)
            return
        }

        if (item.finish == 0L) {
            val notify = Notify.ErrorMessage("Не задан последний день отпуска")
            notify(notify)
            return
        }

        launchSafely {
            repository.upsertVacation(item)
            navigateUp()
        }
    }

}