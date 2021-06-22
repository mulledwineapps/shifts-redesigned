package ru.mulledwine.shifts.extensions

import ru.mulledwine.shifts.data.local.entities.Alarm
import ru.mulledwine.shifts.data.local.entities.AlarmFullParcelable
import ru.mulledwine.shifts.data.local.entities.AlarmParcelable
import ru.mulledwine.shifts.data.local.entities.AlarmView
import ru.mulledwine.shifts.data.local.models.AlarmItem

fun AlarmView.toAlarmParcelable() = AlarmFullParcelable(
    alarm = alarm,
    job = job,
    schedule = schedule,
    shift = shift
)

fun AlarmView.toAlarmItem() = AlarmItem(
    id = alarm.id,
    jobName = job.name,
    shiftName = shift.name,
    time = alarm.time.toString(),
    isActive = alarm.isActive
)

fun Alarm.toAlarmParcelable() = AlarmParcelable(
    id = id!!,
    time = time,
    isActive = isActive,
    label = label
)