package ru.mulledwine.shifts.viewmodels

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mulledwine.shifts.data.local.entities.AlarmParcelable
import ru.mulledwine.shifts.data.local.models.AlarmItem
import ru.mulledwine.shifts.extensions.toAlarmItem
import ru.mulledwine.shifts.extensions.toAlarmParcelable
import ru.mulledwine.shifts.repositories.AlarmsRepository
import ru.mulledwine.shifts.ui.alarms.AlarmsFragmentDirections
import ru.mulledwine.shifts.utils.AlarmCalculator
import ru.mulledwine.shifts.viewmodels.base.EmptyState
import javax.inject.Inject

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val repository: AlarmsRepository
) : BaseViewModel<EmptyState>(handle, EmptyState) {

    companion object {
        private const val TAG = "M_AlarmsViewModel"
    }

    fun observeAlarms(owner: LifecycleOwner,
                      onChange: (list: List<AlarmItem>) -> Unit) {
        repository.findAlarms().map { list -> list.map { it.toAlarmItem() } }
            .observe(owner, Observer(onChange))
    }

    fun handleNavigateEditAlarm(title: String, id: Int) {
        launchSafely {
            val item = repository.getAlarmFull(id).toAlarmParcelable()
            val action = AlarmsFragmentDirections.actionNavAlarmsToNavAlarm(title, item)
            navigateWithAction(action)
        }
    }

    fun handleNavigateAddAlarm(title: String) {
        val action = AlarmsFragmentDirections.actionNavAlarmsToNavAlarm(title)
        navigateWithAction(action)
    }

    fun handleToggleAlarm(
        alarmItem: AlarmItem,
        flagActive: Boolean,
        setAlarm: (alarm: AlarmParcelable, time: Long) -> Unit,
        cancelAlarm: (id: Int) -> Unit
    ) {
        launchSafely {
            val id = alarmItem.id
            val isActiveOnToggle = repository.isActive(id)
            if (isActiveOnToggle == flagActive) return@launchSafely
            repository.toggleAlarm(id)
            Log.d(TAG, "handleToggleAlarm: active ${!isActiveOnToggle} now")
            if (isActiveOnToggle) {
                cancelAlarm(id)
            } else {
                val alarmTime = calculateAlarmTime(id) ?: return@launchSafely
                val alarm = repository.getAlarm(id).toAlarmParcelable()
                setAlarm(alarm, alarmTime)
            }
        }
    }

    fun rescheduleAlarm(id: Int, setAlarm: (time: Long) -> Unit) {
        launchSafely {
            val isExist = repository.isExist(id)
            if (!isExist) return@launchSafely

            val isCyclic = repository.isCyclic(id)
            if (!isCyclic) return@launchSafely

            val alarmTime = calculateAlarmTime(id) ?: return@launchSafely
            setAlarm(alarmTime)
        }
    }

    private suspend fun calculateAlarmTime(id: Int): Long? {

        val alarm = repository.getAlarmFull(id)
        val shiftsCount = repository.getShiftsCount(alarm.schedule.id)

        val calculator = AlarmCalculator.Builder()
            .setIsCyclic(alarm.schedule.isCyclic)
            .setScheduleStart(alarm.schedule.start)
            .setShiftOrdinal(alarm.shift.ordinal)
            .setShiftsCount(shiftsCount)
            .setShiftClockTime(alarm.shift.start)
            .setAlarmClockTime(alarm.alarm.time)
            .build()

        calculator.errorMessage?.let { message ->
            makeToast(message)
            return null
        }

        return calculator.alarmTime
    }

    fun handleDeleteAlarm(id: Int) {
        launchSafely {
            repository.deleteAlarm(id)
        }
    }

    fun handleDeleteAlarms(ids: List<Int>) {
        launchSafely {
            repository.deleteAlarms(ids)
        }
    }

}