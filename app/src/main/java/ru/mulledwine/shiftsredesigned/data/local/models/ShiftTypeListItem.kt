package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShiftTypeListItem(
    val id: Int,
    val name: String,
    val duration: String,
    val color: Int
): Parcelable

//@Parcelize
//data class ShiftTypeItem(
//    val id: Int,
//    val name: String,
//    val color: Int,
//    val alarm
//): Parcelable