package ru.mulledwine.shifts.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VacationItem(
    val id: Int,
    val duration: String,
    val description: String
): Parcelable

// для передачи на экран редактирования
@Parcelize
data class VacationParcelable(
    val id: Int,
    val start: Long,
    val finish: Long,
    val shiftTypeItem: ShiftTypeListItem?
) : Parcelable