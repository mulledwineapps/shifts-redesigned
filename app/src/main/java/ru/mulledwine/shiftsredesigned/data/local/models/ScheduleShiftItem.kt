package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduleShiftItem(
    val shiftId: Int,
    val shiftTypeId: Int,
    val ordinal: Int,
    val duration: String,
    val typeName: String,
    val color: Int,
    val isNewItem: Boolean
): Parcelable