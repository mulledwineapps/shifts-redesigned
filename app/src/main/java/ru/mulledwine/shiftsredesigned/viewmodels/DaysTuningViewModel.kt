package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import ru.mulledwine.shiftsredesigned.data.local.models.JobItem
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTypeItem
import ru.mulledwine.shiftsredesigned.extensions.data.toJobItem
import ru.mulledwine.shiftsredesigned.extensions.data.toShiftTypeItem
import ru.mulledwine.shiftsredesigned.repositories.JobsRepository
import ru.mulledwine.shiftsredesigned.repositories.ShiftTypeRepository
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class DaysTuningViewModel(handle: SavedStateHandle) :
    BaseViewModel<EmptyState>(handle, EmptyState) {

    private val jobsRepository = JobsRepository
    private val shiftTypeRepository = ShiftTypeRepository

    fun observeJobs(owner: LifecycleOwner, onChange: (list: List<JobItem>) -> Unit) {
        jobsRepository.findJobs().map { list -> list.map { it.toJobItem() } }
            .observe(owner, Observer(onChange))
    }

    fun observeShiftTypes(owner: LifecycleOwner, onChange: (list: List<ShiftTypeItem>) -> Unit) {
        shiftTypeRepository.findShiftTypes().map { list ->
            list.map { it.toShiftTypeItem() }
        }.observe(owner, Observer(onChange))
    }

}