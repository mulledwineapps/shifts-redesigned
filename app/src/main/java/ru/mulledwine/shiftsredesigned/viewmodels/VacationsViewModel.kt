package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import ru.mulledwine.shiftsredesigned.data.local.models.JobItem
import ru.mulledwine.shiftsredesigned.data.local.models.JobWithVacationItems
import ru.mulledwine.shiftsredesigned.data.local.models.VacationItem
import ru.mulledwine.shiftsredesigned.extensions.data.toJobItem
import ru.mulledwine.shiftsredesigned.extensions.data.toVacationItem
import ru.mulledwine.shiftsredesigned.extensions.mutableLiveData
import ru.mulledwine.shiftsredesigned.repositories.VacationsRepository
import ru.mulledwine.shiftsredesigned.ui.vacations.VacationsFragmentDirections
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

fun JobWithVacationItems.toVacationsState() = VacationsState(
    this.jobItem.name,
    vacationItems
)

class VacationsViewModel @ViewModelInject constructor(
    @Assisted handle: SavedStateHandle,
    private val repository: VacationsRepository
) : BaseViewModel<VacationsState>(
    handle,
    handle.get<JobWithVacationItems>("item")!!.toVacationsState()
) {

    private val jobLive = mutableLiveData(handle.get<JobWithVacationItems>("item")!!.jobItem)

    private val vacations = jobLive.switchMap {
        repository.findVacations(it.id)
    }

    init {
        subscribeOnDataSource(jobLive) { schedule, state ->
            state.copy(jobName = schedule.name)
        }
        subscribeOnDataSource(vacations) { vacations, state ->
            state.copy(vacationItems = vacations.map {
                it.toVacationItem()
            })
        }
    }

    fun observeJobs(owner: LifecycleOwner, onChange: (list: List<JobItem>) -> Unit) {
        repository.findJobs().map { list -> list.map { it.toJobItem() } }
            .observe(owner, Observer(onChange))
    }

    fun handleUpdateJob(job: JobItem) {
        jobLive.value = job
    }

    fun handleClickAdd(title: String) {
        val action = VacationsFragmentDirections
            .actionNavVacationsToNavVacation(title, jobLive.value!!)
        navigateWithAction(action)
    }

    fun handleClickEdit(title: String, id: Int) {
        launchSafely {
            val vacation = repository.getVacationParcelable(id)
            val action = VacationsFragmentDirections
                .actionNavVacationsToNavVacation(title, jobLive.value!!, vacation)
            navigateWithAction(action)
        }
    }

    fun handleDeleteVacation(id: Int) {
        launchSafely {
            repository.deleteVacation(id)
        }
    }

    fun handleDeleteVacations(ids: List<Int>) {
        launchSafely {
            repository.deleteVacations(ids)
        }
    }

}

data class VacationsState(
    val jobName: String,
    val vacationItems: List<VacationItem>
) : IViewModelState