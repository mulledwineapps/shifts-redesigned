package ru.mulledwine.shifts.viewmodels

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mulledwine.shifts.data.local.entities.Alarm
import ru.mulledwine.shifts.data.local.entities.AlarmFullParcelable
import ru.mulledwine.shifts.data.local.entities.AlarmParcelable
import ru.mulledwine.shifts.data.local.models.*
import ru.mulledwine.shifts.extensions.*
import ru.mulledwine.shifts.extensions.data.getDuration
import ru.mulledwine.shifts.extensions.data.getShiftsForAlarm
import ru.mulledwine.shifts.extensions.data.toJobItem
import ru.mulledwine.shifts.extensions.data.toScheduleItem
import ru.mulledwine.shifts.repositories.AlarmRepository
import ru.mulledwine.shifts.repositories.JobsRepository
import ru.mulledwine.shifts.repositories.SchedulesRepository
import ru.mulledwine.shifts.utils.AlarmCalculator
import ru.mulledwine.shifts.utils.Utils
import ru.mulledwine.shifts.viewmodels.base.IViewModelState
import javax.inject.Inject

fun AlarmFullParcelable.toAlarmState() = AlarmState(
    jobTitle = this.job?.name ?: "",
    scheduleTitle = this.schedule?.getDuration() ?: "",
    shiftTitle = this.shift?.name ?: "",
    alarmClock = this.alarm?.time ?: ClockTime()
)

@HiltViewModel
class AlarmViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val repository: AlarmRepository,
    private val jobRepository: JobsRepository,
    private val schedulesRepository: SchedulesRepository,
) : BaseViewModel<AlarmState>(
    handle,
    handle.get<AlarmFullParcelable>("item")?.toAlarmState() ?: AlarmState()
) {
    // TODO если сделать по умолчанию null? тогда job, schedule и shift смогут быть non-nullable
    private val param: AlarmFullParcelable = handle["item"] ?: AlarmFullParcelable()

    companion object {
        private const val TAG = "M_AlarmViewModel"
    }

    private val alarmParam = param.alarm

    private val jobLive = mutableLiveData(param.job)
    private val scheduleLive = mutableLiveData(param.schedule)
    private val shiftLive = mutableLiveData(param.shift)

    private val alarmClock = mutableLiveData(param.alarm?.time ?: ClockTime())

    private val schedules = jobLive.switchMap {
        schedulesRepository.findSchedulesWithShifts(it.id)
    }

    private val shifts =
        MediatorLiveData<List<ShiftItem>>().apply {
            val f = f@{
                val schedules = schedules.value ?: return@f
                val schedule = scheduleLive.value ?: run {
                    value = emptyList()
                    return@f
                }
                value = schedules.firstOrNull { it.schedule.id == schedule.id }
                    ?.getShiftsForAlarm() ?: emptyList()
            }

            value = emptyList()
            addSource(schedules) { f.invoke() }
            addSource(scheduleLive) { f.invoke() }
        }

    private val alarmInfo =
        MediatorLiveData<String>().apply {
            val f: () -> String = f@{
                if (shifts.value?.count() ?: 0 == 0) return@f ""
                shiftLive.value ?: return@f ""
                alarmClock.value ?: return@f ""
                return@f getAlarmInfo()
            }

            value = ""
            addSource(shifts) { value = f.invoke() }
            addSource(shiftLive) { value = f.invoke() }
            addSource(alarmClock) { value = f.invoke() }
        }

    init {
        subscribeOnDataSource(jobLive) { job, state ->
            state.copy(jobTitle = job?.name ?: "")
        }
        subscribeOnDataSource(scheduleLive) { schedule, state ->
            state.copy(scheduleTitle = schedule?.getDuration() ?: "")
        }
        subscribeOnDataSource(shiftLive) { shift, state ->
            val title = if (shift == null) "" else "${shift.ordinal} - ${shift.name}"
            state.copy(shiftTitle = title)
        }
        subscribeOnDataSource(alarmInfo) { info, state ->
            state.copy(alarmInfo = info)
        }
        subscribeOnDataSource(alarmClock) { clock, state ->
            state.copy(alarmClock = clock)
        }
    }

    fun observeJobs(owner: LifecycleOwner, onChange: (list: List<JobItem>) -> Unit) {
        jobRepository.findJobs().map { list -> list.map { it.toJobItem() } }
            .observe(owner, Observer(onChange))
    }

    fun observeSchedules(owner: LifecycleOwner, onChange: (list: List<ScheduleItem>) -> Unit) {
        schedules.map { list ->
            list.mapIndexed { index, it -> it.schedule.toScheduleItem(index.inc()) }
        }.observe(owner, Observer(onChange))
    }

    fun observeShifts(owner: LifecycleOwner, onChange: (list: List<ShiftItem>) -> Unit) {
        shifts.observe(owner, Observer(onChange))
    }

    fun handleUpdateJob(job: JobItem) {
        if (jobLive.value?.id != job.id) {
            jobLive.value = job
            scheduleLive.value = null
            shiftLive.value = null
        }
    }

    fun handleUpdateSchedule(item: ScheduleItem) {
        if (scheduleLive.value?.id != item.id) {
            launchSafely {
                val schedule = repository.getScheduleForAlarm(item.id)
                scheduleLive.value = schedule
                shiftLive.value = null
            }
        }
    }

    fun handleUpdateShift(item: ShiftItem) {
        if (shiftLive.value?.id != item.id) {
            launchSafely {
                val shift = repository.getShiftForAlarm(item.id)
                shiftLive.value = shift
            }
        }
    }

    fun handleUpdateTime(time: ClockTime) {
        alarmClock.value = time
    }

    fun handleClickSave(
        isActive: Boolean,
        label: String,
        handleAlarm: (alarm: AlarmParcelable, time: Long) -> Unit
    ) {

        if (jobLive.value == null) {
            notify(Notify.TextMessage("Не выбрана работа"))
            return
        }
        if (scheduleLive.value == null) {
            notify(Notify.TextMessage("Не выбран график"))
            return
        }
        if (shiftLive.value == null) {
            notify(Notify.TextMessage("Не выбрана смена"))
            return
        }

        val calculator = AlarmCalculator.Builder()
            .setIsCyclic(scheduleLive.value!!.isCyclic)
            .setScheduleStart(scheduleLive.value!!.start)
            .setShiftOrdinal(shiftLive.value!!.ordinal)
            .setShiftsCount(shifts.value!!.count())
            .setShiftClockTime(shiftLive.value!!.start)
            .setAlarmClockTime(alarmClock.value!!)
            .build()

        calculator.errorMessage?.let { message ->
            makeToast(message) // TODO определиться, тост или снэкбар, как выше
            return
        }

        val alarmTime = calculator.alarmTime

        launchSafely {

            val alarm = Alarm(
                id = alarmParam?.id,
                shiftId = shiftLive.value!!.id,
                time = alarmClock.value!!,
                isActive = isActive,
                label = label
            )

            val alarmParcelable = if (alarmParam == null) {
                val id = repository.createAlarm(alarm).toInt()
                repository.getAlarm(id).toAlarmParcelable()
            } else {
                repository.updateAlarm(alarm)
                alarmParam
            }

            // TODO Если возникла ошибка, не сохранять будильник
            // какая у андроида защита от кривых повторяющихся будильников?
            // TODO контролировать минимальный интервал между будильниками

            handleAlarm(alarmParcelable, alarmTime)
            if (isActive) {
                notify(Notify.TextMessage("Будильник сработает ${alarmTime.whenHappen()}"))
            } else {
                makeToast("Будильник выключен")
            }

            navigateUp()
        }
    }

    private fun getAlarmInfo(): String {
        val now = Utils.getTime()
        val shiftStart = getShiftStart()
        val duration = shiftLive.value!!.finish - shiftLive.value!!.start
        val shiftFinish = shiftStart + duration.toLong()
        if (shiftFinish - now < 0L) {
            return "Смена уже закончилась"
        }
        if (shiftStart - now < 0L) {
            return "Начало смены уже прошло"
        }
        val alarmTime = getAlarmTime(shiftStart)
        val delta = alarmTime - now
        if (delta < 0L) {
            return "Время будильника уже прошло"
        }
        val date = alarmTime.toDate()
        return "Будильник сработает ${date.format()}"
    }

    private fun getShiftStart(): Long {
        return if (scheduleLive.value!!.isCyclic) getShiftStartCyclic()
        else getShiftStartNonCyclic()
    }

    private fun getShiftStartCyclic(): Long {
        val firstShiftStart = getShiftStartNonCyclic()
        val now = Utils.getTime()
        val cycleDuration = getAlarmPeriod()
        // количество прошедших полных циклов от смены до смены
        val cyclesPassed = (now - firstShiftStart) / cycleDuration
        return firstShiftStart + (cyclesPassed + 1) * cycleDuration // ближайшее начало смены
    }

    private fun getShiftStartNonCyclic(): Long {
        val scheduleStart = scheduleLive.value!!.start
        val shiftDelta = TimeUnits.DAY.value * (shiftLive.value!!.ordinal - 1)
        val shiftClock = shiftLive.value!!.start

        return scheduleStart + shiftDelta + shiftClock.toLong()
    }

    private fun getAlarmPeriod(): Long {
        return shifts.value!!.count() * TimeUnits.DAY.value
    }

    private fun getAlarmTime(shiftStart: Long): Long {
        // all starts should be 0 milliseconds!
        // all finishes should be last millisecond of a day!

        val shiftClock = shiftLive.value!!.start

        val alarmDelta = if (alarmClock.value!! <= shiftClock) {
            // same day: 10:30 смена 09:00 будильник
            shiftClock - alarmClock.value!!
        } else {
            // previous day: 01:00 смена 23:00 будильник
            shiftClock + ClockTime(24, 0) - alarmClock.value!!
        }

        var time = shiftStart - alarmDelta.toLong()

        // для цикличных графиков:
        // если смена ещё не началась (будет сегодня вечером)
        // а время будильника уже прошло (было утром)
        if (scheduleLive.value!!.isCyclic) {
            val now = Utils.getTime()
            val cycleDuration = getAlarmPeriod()
            if (time < now) {
                time += cycleDuration
            }
        }

        Log.d(TAG, "alarm ${time.toDate()}")
        return time
    }

}

data class AlarmState(
    val jobTitle: String = "",
    val scheduleTitle: String = "",
    val shiftTitle: String = "",
    val alarmClock: ClockTime = ClockTime(),
    val alarmInfo: String = ""
) : IViewModelState