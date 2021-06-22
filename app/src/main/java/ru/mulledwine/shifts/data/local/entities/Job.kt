package ru.mulledwine.shifts.data.local.entities

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String
): Parcelable {
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
