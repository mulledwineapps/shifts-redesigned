package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShiftTypeListItem(
    val id: Int,
    val name: String,
    val title: String, // duration
    val color: Int
): Parcelable