package ru.mulledwine.shiftsredesigned.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.data.local.models.TimeUnits
import ru.mulledwine.shiftsredesigned.extensions.toCalendar
import java.util.*

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val start: Long,
    val finish: Long = 0L
) {
    companion object {
        private var counter = 0
        private val scheduleNames = listOf(
            "Мой график",
            "График друга",
            "2 через 2",
            "2 через 3"
        )

        fun generateAll(): List<Schedule> {
            return scheduleNames.map { scheduleName ->
                val time = Constants.today.timeInMillis +
                        (-10 * TimeUnits.WEEK.value..-TimeUnits.WEEK.value).random()
                Schedule(
                    id = counter++,
                    name = scheduleName,
                    start = time
                )
            }.toList()
        }
    }
}

data class ScheduleWithShifts(
    @Embedded val schedule: Schedule,
    @Relation(
        parentColumn = "id",
        entityColumn = "schedule_id"
    )
    val shiftsWithTypes: List<ShiftsWithTypeView>
)