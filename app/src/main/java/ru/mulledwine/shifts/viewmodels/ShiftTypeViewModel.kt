package ru.mulledwine.shifts.viewmodels

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mulledwine.shifts.data.local.entities.ShiftType
import ru.mulledwine.shifts.repositories.ShiftTypeRepository
import ru.mulledwine.shifts.viewmodels.base.EmptyState
import javax.inject.Inject

@HiltViewModel
class ShiftTypeViewModel @Inject constructor(
    handle: SavedStateHandle,
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