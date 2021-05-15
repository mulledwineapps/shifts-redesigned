package ru.mulledwine.shiftsredesigned.extensions

import ru.mulledwine.shiftsredesigned.data.local.entities.AlarmParcelable
import ru.mulledwine.shiftsredesigned.data.local.entities.AlarmView
import ru.mulledwine.shiftsredesigned.data.local.models.AlarmItem

fun AlarmView.toAlarmParcelable() = AlarmParcelable(
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