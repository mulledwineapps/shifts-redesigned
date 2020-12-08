package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduleParcelable(
    val id: Int,
    val name: String,
    val start: Long,
    val finish: Long,
    val shiftItems: List<ScheduleShiftItem>
) : Parcelable