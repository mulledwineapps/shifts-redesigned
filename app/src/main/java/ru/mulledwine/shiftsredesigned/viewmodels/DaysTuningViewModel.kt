package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleItem
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTypeItem
import ru.mulledwine.shiftsredesigned.extensions.data.toScheduleItem
import ru.mulledwine.shiftsredesigned.extensions.data.toShiftTypeItem
import ru.mulledwine.shiftsredesigned.repositories.DaysTuningRepository
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class DaysTuningViewModel(handle: SavedStateHandle) :
    BaseViewModel<EmptyState>(handle, EmptyState) {

    private val repository = DaysTuningRepository

    fun observeSchedules(owner: LifecycleOwner, onChange: (list: List<ScheduleItem>) -> Unit) {
        repository.findSchedules().map { list -> list.map { it.toScheduleItem() } }
            .observe(owner, Observer(onChange))
    }

    fun observeShiftTypes(owner: LifecycleOwner, onChange: (list: List<ShiftTypeItem>) -> Unit) {
        repository.findShiftTypes().map { list ->
            list.map { it.toShiftTypeItem() }
        }.observe(owner, Observer(onChange))
    }

}