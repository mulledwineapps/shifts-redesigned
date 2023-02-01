package ru.mulledwine.shifts.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mulledwine.shifts.data.local.models.ShiftTypeListItem
import ru.mulledwine.shifts.extensions.data.toShiftTypeItem
import ru.mulledwine.shifts.repositories.ShiftTypesRepository
import ru.mulledwine.shifts.ui.shifttypes.ShiftTypesFragmentDirections
import ru.mulledwine.shifts.viewmodels.base.EmptyState
import javax.inject.Inject

@HiltViewModel
class ShiftTypesViewModel @Inject constructor(
    handle: SavedStateHandle,
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