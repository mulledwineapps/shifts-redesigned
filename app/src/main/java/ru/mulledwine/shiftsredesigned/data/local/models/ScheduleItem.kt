package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduleItem(
    val id: Int,
    val name: String,
    val duration: String
): Parcelable