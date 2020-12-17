package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduleItem(
    val id: Int,
    val name: String,
    val duration: String,
    val isCyclic: Boolean
) : Parcelable

// для передачи на экран редактирования
@Parcelize
data class ScheduleWithShiftItems(
    val id: Int,
    val name: String,
    val isCyclic: Boolean,
    val start: Long,
    val finish: Long,
    val shiftItems: List<ScheduleShiftItem>
) : Parcelable

@Parcelize
data class ScheduleShiftItem(
    val shiftId: Int,
    val shiftTypeId: Int,
    val ordinal: Int,
    val duration: String,
    val typeName: String,
    val color: Int,
    val isNewItem: Boolean
) : Parcelable

@Parcelize
data class ScheduleShort(
    val id: Int,
    val name: String,
    val isCyclic: Boolean
) : Parcelable

@Parcelize
data class ScheduleWithVacationItems(
    val schedule: ScheduleShort,
    val vacationItems: List<VacationItem>
) : Parcelable