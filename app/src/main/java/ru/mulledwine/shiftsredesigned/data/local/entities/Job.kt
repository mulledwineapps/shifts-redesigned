package ru.mulledwine.shiftsredesigned.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String
) {
    companion object {
        private var counter = 0
        private val jobNames = listOf(
            "Основная работа",
            "Вторая работа"
        )

        fun generate(): List<Job> {
            return jobNames.map {
                Job(
                    id = counter++,
                    name = it
                )
            }
        }
    }
}

data class JobWithSchedules(
    @Embedded val job: Job,
    @Relation(
        parentColumn = "id",
        entityColumn = "job_id"
    )
    val schedules: List<Schedule>
)
