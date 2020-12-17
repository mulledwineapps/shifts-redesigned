package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Month(
    val number: Int, // с нуля
    val year: Int
) : Parcelable