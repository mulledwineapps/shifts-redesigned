package ru.mulledwine.shifts.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mulledwine.shifts.Constants
import ru.mulledwine.shifts.data.local.entities.Job
import ru.mulledwine.shifts.data.local.models.JobItem
import ru.mulledwine.shifts.data.local.models.JobWithStatisticItems
import ru.mulledwine.shifts.data.local.models.Month
import ru.mulledwine.shifts.extensions.data.getStatisticItems
import ru.mulledwine.shifts.extensions.data.toJobItem
import ru.mulledwine.shifts.extensions.month
import ru.mulledwine.shifts.extensions.year
import ru.mulledwine.shifts.repositories.DaysRepository
import ru.mulledwine.shifts.repositories.JobsRepository
import ru.mulledwine.shifts.repositories.SchedulesRepository
import ru.mulledwine.shifts.repositories.VacationsRepository
import ru.mulledwine.shifts.ui.jobs.JobsFragmentDirections
import ru.mulledwine.shifts.viewmodels.base.EmptyState
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val daysRepository: DaysRepository,
    private val jobsRepository: JobsRepository,
    private val schedulesRepository: SchedulesRepository,
    private val vacationsRepository: VacationsRepository
) : BaseViewModel<EmptyState>(handle, EmptyState) {

    fun observeJobs(owner: LifecycleOwner, onChange: (list: List<JobItem>) -> Unit) {
        jobsRepository.findJobs().map { list -> list.map { it.toJobItem() } }
            .observe(owner, Observer(onChange))
    }

    fun navigateJobDialog(item: JobItem? = null) {
        val action = JobsFragmentDirections.actionNavJobsToDialogJob(item)
        navigateWithAction(action)
    }
    
    fun navigateToStatistics(jobId: Int) {

        launchSafely {
            val job = jobsRepository.getJob(jobId).toJobItem()
            val month = Month(Constants.today.month, Constants.today.year)
            val days = daysRepository.getDays(month)
            val schedules = schedulesRepository.getSchedulesWithShifts(jobId)
            val vacations = vacationsRepository.getVacations(jobId)
            val statisticItems = days.getStatisticItems(schedules, vacations)

            val item = JobWithStatisticItems(job, month, statisticItems)
            val action = JobsFragmentDirections.actionNavJobsToNavStatistics(item)
            navigateWithAction(action)
        }
    }

    fun handleAddJob(name: String) {
        launchSafely {
            val job = Job(name = name)
            jobsRepository.insertJob(job)
        }
    }

    fun handleUpdateJob(id: Int, name: String) {
        launchSafely {
            val job = Job(id, name)
            jobsRepository.updateJob(job)
        }
    }

    fun handleDeleteJob(id: Int) {
        launchSafely {
            jobsRepository.deleteJob(id)
        }
    }

    fun handleDeleteJobs(ids: List<Int>) {
        launchSafely {
            jobsRepository.deleteJobs(ids)
        }
    }

}