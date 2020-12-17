package ru.mulledwine.shiftsredesigned.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "vacations",
    foreignKeys = [
        ForeignKey(
            entity = Job::class,
            parentColumns = ["id"],
            childColumns = ["job_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Shift::class,
            parentColumns = ["id"],
            childColumns = ["first_shift_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Vacation(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "job_id", index = true)
    val jobId: Int,
    val start: Long,
    val finish: Long,
    @ColumnInfo(name = "first_shift_id", index = true)
    val firstShiftId: Int? // первая рабочая смена после отпуска
)