package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTypeListItem
import ru.mulledwine.shiftsredesigned.extensions.data.toShiftTypeItem
import ru.mulledwine.shiftsredesigned.repositories.ShiftTypesRepository
import ru.mulledwine.shiftsredesigned.ui.shifttypes.ShiftTypesFragmentDirections
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class ShiftTypesViewModel @ViewModelInject constructor(
    @Assisted handle: SavedStateHandle,
    private val repository: ShiftTypesRepository
) : BaseViewModel<EmptyState>(handle, EmptyState) {


    fun observeShiftTypes(
        owner: LifecycleOwner,
        onChange: (list: List<ShiftTypeListItem>) -> Unit
    ) {
        repository.findShiftTypes().map { list ->
            list.map { it.toShiftTypeItem() }
        }.observe(owner, Observer(onChange))
    }

    fun handleNavigateEditShiftType(title: String, id: Int) {
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