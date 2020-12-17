package ru.mulledwine.shiftsredesigned.data.local.entities

import androidx.room.*
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTypeItem

@Entity(
    tableName = "vacations",
    foreignKeys = [
        ForeignKey(
            entity = Schedule::class,
            parentColumns = ["id"],
            childColumns = ["schedule_id"],
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
    @ColumnInfo(name = "schedule_id", index = true)
    val scheduleId: Int,
    val start: Long,
    val finish: Long,
    @ColumnInfo(name = "first_shift_id", index = true)
    val firstShiftId: Int? // первая рабочая смена после отпуска
)