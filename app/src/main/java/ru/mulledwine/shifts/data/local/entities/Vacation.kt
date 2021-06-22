package ru.mulledwine.shifts.data.local.entities

import androidx.room.*

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
    val finish: Long, // нулевая миллисекунда последнего дня отпуска
    @ColumnInfo(name = "first_shift_id", index = true)
    val firstShiftId: Int? // первая рабочая смена после отпуска
)

data class VacationWithJob(
    @Embedded val content: Vacation,
    @Relation(
        parentColumn = "job_id",
        entityColumn = "id"
    )
    val job: Job
)