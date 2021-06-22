package ru.mulledwine.shifts.data.local.models

data class AlarmItem(
    val id: Int,
    val jobName: String,
    val shiftName: String,
    val time: String,
    val isActive: Boolean
)