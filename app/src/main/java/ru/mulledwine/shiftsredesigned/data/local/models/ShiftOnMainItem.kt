package ru.mulledwine.shiftsredesigned.data.local.models

data class ShiftOnMainItem(
    val id: Int,
    val scheduleId: Int,
    val scheduleName: String,
    val description: String, // тип смены и её продолжительность
    val color: Int,
    val shiftsCount: Int
)