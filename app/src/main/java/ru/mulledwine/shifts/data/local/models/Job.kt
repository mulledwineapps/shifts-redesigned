package ru.mulledwine.shifts.data.local.models

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

@Parcelize
data class JobWithVacationItems(
    val jobItem: JobItem,
    val vacationItems: List<VacationItem>
) : Parcelable

@Parcelize
data class JobWithStatisticItems(
    val jobItem: JobItem,
    val month: Month,
    val statisticItems: List<StatisticItem>
) : Parcelable