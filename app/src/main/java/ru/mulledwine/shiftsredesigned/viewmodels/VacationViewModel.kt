package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftWithType
import ru.mulledwine.shiftsredesigned.data.local.entities.Vacation
import ru.mulledwine.shiftsredesigned.repositories.VacationRepository
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class VacationViewModel @ViewModelInject constructor(
    @Assisted handle: SavedStateHandle,
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