package ru.mulledwine.shiftsredesigned.data.local.entities

import androidx.room.*
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.data.local.models.TimeUnits
import ru.mulledwine.shiftsredesigned.extensions.data.IDurable
import ru.mulledwine.shiftsredesigned.extensions.data.getDuration

@Entity(
    tableName = "schedules",
    foreignKeys = [
        ForeignKey(
            entity = Job::class,
            parentColumns = ["id"],
            childColumns = ["job_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "job_id", index = true)
    val jobId: Int,
    @ColumnInfo(name = "is_cyclic")
    val isCyclic: Boolean,
    override val start: Long,
    override val finish: Long = 0L
): IDurable {
    companion object {
        private var counter = 0

        fun generate(jobId: Int): Schedule {
            val time = Constants.today.timeInMillis + TimeUnits.WEEK.value * (-10..-1).random()
            return Schedule(
                id = counter++,
                jobId = jobId,
                isCyclic = true,
                start = time
            )
        }

    }
}

data class ScheduleWithShifts(
    @Embedded val schedule: Schedule,
    @Relation(
        parentColumn = "job_id",
        entityColumn = "id"
    ) val job: Job,
    @Relation(
        parentColumn = "id",
        entityColumn = "schedule_id"
    )
    val shiftsWithTypes: List<ShiftsWithTypeView>
)

data class JobWithVacations(
    @Embedded val job: Job,
    @Relation(
        parentColumn = "id",
        entityColumn = "job_id"
    )
    val vacations: List<Vacation>
)