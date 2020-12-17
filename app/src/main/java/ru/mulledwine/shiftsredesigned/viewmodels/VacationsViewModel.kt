package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.*
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleItem
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleShort
import ru.mulledwine.shiftsredesigned.data.local.models.ScheduleWithVacationItems
import ru.mulledwine.shiftsredesigned.data.local.models.VacationItem
import ru.mulledwine.shiftsredesigned.extensions.data.toScheduleItem
import ru.mulledwine.shiftsredesigned.extensions.data.toVacationItem
import ru.mulledwine.shiftsredesigned.extensions.mutableLiveData
import ru.mulledwine.shiftsredesigned.repositories.VacationsRepository
import ru.mulledwine.shiftsredesigned.ui.vacations.VacationsFragmentDirections
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

class VacationsViewModel(
    handle: SavedStateHandle,
    param: ScheduleWithVacationItems
) : BaseViewModel<VacationsState>(
    handle,
    VacationsState(param.schedule.name, param.vacationItems)
) {

    private val repository = VacationsRepository

    private val scheduleLive = mutableLiveData(param.schedule)

    private val vacations = scheduleLive.switchMap {
        repository.findVacations(it.id)
    }

    init {
        subscribeOnDataSource(scheduleLive) { schedule, state ->
            state.copy(scheduleName = schedule.name)
        }
        subscribeOnDataSource(vacations) { vacations, state ->
            state.copy(vacationItems = vacations.map {
                it.toVacationItem()
            })
        }
    }

    fun observeSchedules(owner: LifecycleOwner, onChange: (list: List<ScheduleItem>) -> Unit) {
        repository.findSchedules().map { list -> list.map { it.toScheduleItem() } }
            .observe(owner, Observer(onChange))
    }

    fun handleUpdateSchedule(schedule: ScheduleShort) {
        scheduleLive.value = schedule
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

    fun handleClickAdd(title: String) {
        val action = VacationsFragmentDirections
            .actionNavVacationsToNavVacation(title, scheduleLive.value!!)
        navigateWithAction(action)
    }

    fun handleClickEdit(title: String, id: Int) {
        launchSafely {
            val vacation = repository.getVacationParcelable(id)
            val action = VacationsFragmentDirections
                .actionNavVacationsToNavVacation(title, scheduleLive.value!!, vacation)
            navigateWithAction(action)
        }
    }

}

data class VacationsState(
    val scheduleName: String,
    val vacationItems: List<VacationItem>
) : IViewModelState