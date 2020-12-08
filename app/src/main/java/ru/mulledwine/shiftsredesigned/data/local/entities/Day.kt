package ru.mulledwine.shiftsredesigned.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "days")
data class Day(
    @PrimaryKey
    val id: String,
    val date: Int, // порядковый номер дня в месяце (с единицы)
    val numberInWeek: Int, // порядковый номер дня в неделе (с нуля)
    val start: Calendar
)