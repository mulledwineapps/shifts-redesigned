package ru.mulledwine.shifts.viewmodels

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mulledwine.shifts.viewmodels.base.EmptyState
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    handle: SavedStateHandle
) : BaseViewModel<EmptyState>(handle, EmptyState) {
    companion object {
        private const val TAG = "M_SettingsViewModel"
    }
}