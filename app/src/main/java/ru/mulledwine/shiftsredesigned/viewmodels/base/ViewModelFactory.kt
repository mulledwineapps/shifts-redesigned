package ru.mulledwine.shiftsredesigned.viewmodels.base

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleWithVacationItems
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
                params as ScheduleWithVacationItems
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}