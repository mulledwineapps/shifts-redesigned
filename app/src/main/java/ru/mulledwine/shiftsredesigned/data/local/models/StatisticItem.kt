package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class StatisticItem: Parcelable {

    companion object {
        const val VIEW_TYPE_ELEMENT = 0
        const val VIEW_TYPE_GROUP = 1
    }

    abstract val viewType: Int
    abstract fun getId(): String

    @Parcelize
    data class Element(
        val title: String,
        val value: String
    ) : StatisticItem(), Parcelable {
        override val viewType: Int = VIEW_TYPE_ELEMENT
        override fun getId(): String = title
    }

    @Parcelize
    data class Group(
        val title: String
    ) : StatisticItem(), Parcelable {
        override val viewType: Int = VIEW_TYPE_GROUP
        override fun getId(): String = title
    }

}