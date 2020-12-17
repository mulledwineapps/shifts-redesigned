package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JobItem(
    val id: Int,
    val name: String
) : Parcelable

@Parcelize
data class JobWithScheduleItems(
    val jobItem: JobItem,
    val scheduleItems: List<ScheduleItem>
) : Parcelable