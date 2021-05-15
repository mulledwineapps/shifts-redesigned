package ru.mulledwine.shiftsredesigned.data.local.models

data class AlarmItem(
    val id: Int,
    val jobName: String,
    val shiftName: String,
    val time: String,
    val isActive: Boolean
)