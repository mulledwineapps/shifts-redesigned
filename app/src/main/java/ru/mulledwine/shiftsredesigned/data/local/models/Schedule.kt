package ru.mulledwine.shiftsredesigned.data.local.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize
import ru.mulledwine.shiftsredesigned.extensions.data.IDurable

@Parcelize
data class ScheduleItem(
    val id: Int,
    val ordinal: Int,
    val duration: String,
    val isCyclic: Boolean
) : Parcelable

@Parcelize
data class ScheduleForAlarm(
    val id: Int,
    @ColumnInfo(name = "is_cyclic")
    val isCyclic: Boolean,
    override val start: Long,
    override val finish: Long
): Parcelable, IDurable

// для передачи на экран редактирования
@Parcelize
data class ScheduleWithShiftItems(
    val id: Int,
    val isCyclic: Boolean,
    val start: Long,
    val finish: Long,
    val shiftItems: List<ScheduleShiftItem>
) : Parcelable

@Parcelize
data class ScheduleShiftItem(
    val shiftId: Int,
    val shiftTypeId: Int,
    val ordinal: Int,
    val title: String, // shift duration
    val typeName: String,
    val color: Int,
    val isNewItem: Boolean,
    val originalShiftTypeId: Int
) : Parcelable