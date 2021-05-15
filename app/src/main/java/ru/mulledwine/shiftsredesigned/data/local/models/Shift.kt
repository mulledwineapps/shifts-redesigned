package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.extensions.data.getDescription

data class ShiftOnMainItem(
    val jobId: Int,
    val jobName: String,
    val scheduleId: Int? = null,
    val scheduleTitle: String? = null,
    val shiftId: Int? = null,
    val shiftDetails: String? = null, // тип смены и её продолжительность
    val color: Int? = null,
    val vacationId: Int? = null,
    val vacationTitle: String? = null
)

data class JobScheduleShiftType(
    val jobId: Int,
    val jobName: String,
    val scheduleId: Int,
    val scheduleTitle: String,
    val shiftId: Int,
    val shiftType: ShiftType,
) {
    fun toShiftOnMainItem(vacationId: Int?, vacationTitle: String?) = ShiftOnMainItem(
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
    val duration: Long
) : Parcelable