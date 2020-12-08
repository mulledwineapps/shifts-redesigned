package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.SavedStateHandle
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class SettingsViewModel(handle: SavedStateHandle) : BaseViewModel<EmptyState>(handle, EmptyState) {
    companion object {
        private const val TAG = "M_SettingsViewModel"
    }
}