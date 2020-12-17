package ru.mulledwine.shiftsredesigned.data.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "days")
data class Day(
    @PrimaryKey
    val id: String,
    val date: Int, // порядковый номер дня в месяце (с единицы)
    val month: Int, // порядковый номер месяца (с нуля)
    val numberInWeek: Int, // порядковый номер дня в неделе (с нуля)
    val startTime: Long // время начала дня (0 минут 0 секунд)
): Parcelable