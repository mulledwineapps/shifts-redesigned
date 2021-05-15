package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//import kotlinx.parcelize.Parcelize

@Parcelize
data class ClockTime(
    val hour: Int = 0,
    val minute: Int = 0
) : Parcelable {

    companion object {
        const val separator = ":"

        fun random() = ClockTime((0..23).random(), 10 * (1..5).random())
    }

   override fun toString(): String {
        val hour = "$hour".padStart(2, '0')
        val minute = "$minute".padStart(2, '0')
        return "$hour$separator$minute"
    }
}