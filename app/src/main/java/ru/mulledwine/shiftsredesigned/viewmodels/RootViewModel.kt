package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class RootViewModel @ViewModelInject constructor(
    @Assisted handle: SavedStateHandle
) : BaseViewModel<EmptyState>(handle, EmptyState) {
    companion object {
        private const val TAG = "M_RootViewModel"
    }
}