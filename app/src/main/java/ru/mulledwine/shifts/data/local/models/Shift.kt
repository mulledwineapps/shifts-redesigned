package ru.mulledwine.shifts.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.mulledwine.shifts.data.local.entities.ShiftType
import ru.mulledwine.shifts.extensions.data.getDescription
import ru.mulledwine.shifts.ui.main.MainAdapter
import ru.mulledwine.shifts.ui.main.MainItem

data class JobScheduleShiftType(
    val jobId: Int,
    val jobName: String,
    val scheduleId: Int,
    val scheduleTitle: String,
    val shiftId: Int,
    val shiftType: ShiftType,
) {
    fun toShiftOnMainItem(vacationId: Int?, vacationTitle: String?) = MainItem.Element(
        jobId = jobId,
        jobName = jobName,
        scheduleId = scheduleId,
        scheduleTitle = scheduleTitle,
        shiftId = shiftId,
        shiftDetails = shiftType.getDescription(),
        color = shiftType.color,
        vacationId = vacationId,
        vacationTitle = vacationTitle
    )
}

@Parcelize
data class ShiftItem(
    val id: Int,
    val ordinal: Int,
    val typeName: String,
    val duration: String,
    val color: Int
) : Parcelable

@Parcelize
data class ShiftForAlarm(
    val id: Int,
    val ordinal: Int,
    val name: String,
    val start: ClockTime,
    val finish: ClockTime
) : Parcelable