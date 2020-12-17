package ru.mulledwine.shiftsredesigned.viewmodels.base

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import ru.mulledwine.shiftsredesigned.data.local.models.JobWithScheduleItems
import ru.mulledwine.shiftsredesigned.data.local.models.JobWithVacationItems
import ru.mulledwine.shiftsredesigned.viewmodels.SchedulesViewModel
import ru.mulledwine.shiftsredesigned.viewmodels.VacationsViewModel

class ViewModelFactory(
    owner: SavedStateRegistryOwner, // owner будет сохранять saved state в bundle
    defaultArgs: Bundle = bundleOf(),
    private val params: Any
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(VacationsViewModel::class.java)) {
            return VacationsViewModel(
                handle,
                params as JobWithVacationItems
            ) as T
        }
        if (modelClass.isAssignableFrom(SchedulesViewModel::class.java)) {
            return SchedulesViewModel(
                handle,
                params as JobWithScheduleItems
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}