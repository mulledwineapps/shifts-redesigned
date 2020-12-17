package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftWithType
import ru.mulledwine.shiftsredesigned.data.local.entities.Vacation
import ru.mulledwine.shiftsredesigned.repositories.VacationRepository
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class VacationViewModel(handle: SavedStateHandle) : BaseViewModel<EmptyState>(handle, EmptyState) {

    private val repository = VacationRepository

    fun observePattern(
        scheduleId: Int,
        owner: LifecycleOwner,
        onChange: (list: List<ShiftWithType>) -> Unit
    ) {
        repository.findShifts(scheduleId).observe(owner, Observer(onChange))
    }

    fun handleClickSave(item: Vacation) {
        launchSafely {
            repository.upsertVacation(item)
            navigateUp()
        }
    }

}