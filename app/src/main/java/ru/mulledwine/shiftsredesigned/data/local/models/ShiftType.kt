package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShiftTypeItem(
    val id: Int,
    val name: String
) : Parcelable
