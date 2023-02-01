package ru.mulledwine.shifts.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mulledwine.shifts.data.local.models.JobItem
import ru.mulledwine.shifts.data.local.models.ShiftTypeListItem
import ru.mulledwine.shifts.extensions.data.toJobItem
import ru.mulledwine.shifts.extensions.data.toShiftTypeItem
import ru.mulledwine.shifts.repositories.JobsRepository
import ru.mulledwine.shifts.repositories.ShiftTypeRepository
import ru.mulledwine.shifts.viewmodels.base.EmptyState
import javax.inject.Inject

@HiltViewModel
class DaysTuningViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val jobsRepository: JobsRepository,
    private val shiftTypeRepository: ShiftTypeRepository
) : BaseViewModel<EmptyState>(handle, EmptyState) {

    fun observeJobs(owner: LifecycleOwner, onChange: (list: List<JobItem>) -> Unit) {
        jobsRepository.findJobs().map { list -> list.map { it.toJobItem() } }
            .observe(owner, Observer(onChange))
    }

    fun observeShiftTypes(
        owner: LifecycleOwner,
        onChange: (list: List<ShiftTypeListItem>) -> Unit
    ) {
        shiftTypeRepository.findShiftTypes().map { list ->
            list.map { it.toShiftTypeItem() }
        }.observe(owner, Observer(onChange))
    }

}