package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.repositories.ShiftTypeRepository
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class ShiftTypeViewModel @ViewModelInject constructor(
    @Assisted handle: SavedStateHandle,
    private val repository: ShiftTypeRepository
) : BaseViewModel<EmptyState>(handle, EmptyState) {

    companion object {
        private const val TAG = "M_ShiftTypeViewModel"
    }

    fun handleClickSave(item: ShiftType) {

        if (item.name.isEmpty()) {
            val message = Notify.TextMessage("Название смены не должно быть пустым")
            notify(message)
            return
        }

        launchSafely {
            if (item.id == null) repository.createShiftType(item)
            else repository.updateShiftType(item)
            navigateUp()
        }
    }

}