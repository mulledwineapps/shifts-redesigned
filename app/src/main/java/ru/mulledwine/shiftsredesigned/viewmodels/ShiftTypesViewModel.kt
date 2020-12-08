package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTypeItem
import ru.mulledwine.shiftsredesigned.extensions.data.toShiftTypeItem
import ru.mulledwine.shiftsredesigned.repositories.ShiftTypesRepository
import ru.mulledwine.shiftsredesigned.ui.shifttypes.ShiftTypesFragmentDirections
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class ShiftTypesViewModel(handle: SavedStateHandle) :
    BaseViewModel<EmptyState>(handle, EmptyState) {

    private val repository = ShiftTypesRepository

    fun observeShiftTypes(owner: LifecycleOwner, onChange: (list: List<ShiftTypeItem>) -> Unit) {
        repository.findShiftTypes().map { list ->
            list.map { it.toShiftTypeItem() }
        }.observe(owner, Observer(onChange))
    }

    fun handleEditShiftType(title: String, id: Int) {
        launchSafely {
            val item = repository.getShiftType(id)
            val action = ShiftTypesFragmentDirections.actionNavShiftTypesToNavShiftType(title, item)
            navigateWithAction(action)
        }
    }

    fun handleNavigateAddShiftType(title: String) {
        val action = ShiftTypesFragmentDirections.actionNavShiftTypesToNavShiftType(title)
        navigateWithAction(action)
    }

    fun handleDeleteShiftType(id: Int) {
        launchSafely {
            repository.deleteShiftType(id)
        }
    }

}