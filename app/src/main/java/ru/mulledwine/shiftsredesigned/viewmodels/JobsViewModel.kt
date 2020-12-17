package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import ru.mulledwine.shiftsredesigned.data.local.entities.Job
import ru.mulledwine.shiftsredesigned.data.local.models.JobItem
import ru.mulledwine.shiftsredesigned.extensions.data.toJobItem
import ru.mulledwine.shiftsredesigned.repositories.JobsRepository
import ru.mulledwine.shiftsredesigned.ui.jobs.JobsFragmentDirections
import ru.mulledwine.shiftsredesigned.viewmodels.base.EmptyState

class JobsViewModel(handle: SavedStateHandle) : BaseViewModel<EmptyState>(handle, EmptyState) {

    private val repository = JobsRepository

    fun observeJobs(owner: LifecycleOwner, onChange: (list: List<JobItem>) -> Unit) {
        repository.findJobs().map { list -> list.map { it.toJobItem() } }
            .observe(owner, Observer(onChange))
    }

    fun navigateJobDialog(item: JobItem? = null) {
        val action = JobsFragmentDirections.actionNavJobsToDialogJob(item)
        navigateWithAction(action)
    }

    fun handleAddJob(name: String) {
        launchSafely {
            val job = Job(name = name)
            repository.insertJob(job)
        }
    }

    fun handleUpdateJob(id: Int, name: String) {
        launchSafely {
            val job = Job(id, name)
            repository.updateJob(job)
        }
    }

    fun handleDeleteJob(id: Int) {
        launchSafely {
            repository.deleteJob(id)
        }
    }

    fun handleDeleteJobs(ids: List<Int>) {
        launchSafely {
            repository.deleteJobs(ids)
        }
    }

}