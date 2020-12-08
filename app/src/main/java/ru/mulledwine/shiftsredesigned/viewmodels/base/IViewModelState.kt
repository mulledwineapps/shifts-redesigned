package ru.mulledwine.shiftsredesigned.viewmodels.base

import androidx.lifecycle.SavedStateHandle
import ru.mulledwine.shiftsredesigned.viewmodels.BaseViewModel

interface IViewModelState {
    /**
     * override this if need to save state in bundle
     */
    fun save(outState: SavedStateHandle) {
        // default empty implementation
    }

    /**
     * override this if need to restore state from bundle
     */
    fun restore(savedState: SavedStateHandle): IViewModelState {
        // default empty implementation
        return this
    }
}

object EmptyState: IViewModelState

class EmptyViewModel(handle: SavedStateHandle): BaseViewModel<EmptyState>(handle, EmptyState)