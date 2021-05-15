package ru.mulledwine.shiftsredesigned.data.local.entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import ru.mulledwine.shiftsredesigned.data.local.models.*

@Parcelize
@Entity(
    tableName = "alarms",
    foreignKeys = [
        ForeignKey(
            entity = Shift::class,
            parentColumns = ["id"],
            childColumns = ["shift_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "shift_id", index = true)
    val shiftId: Int,
    val time: ClockTime,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    val label: String
) : Parcelable

@Parcelize
data class AlarmItem(
    val id: Int,
    val time: ClockTime,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    val label: String
) : Parcelable

@DatabaseView(
    viewName = "alarms_view",
    value = """
        SELECT a.id as alarm_id, a.time as alarm_time, a.is_active as alarm_is_active,
        j.id as job_id, j.name as job_name,
        sch.id as schedule_id, sch.is_cyclic as schedule_is_cyclic,
        sch.start as schedule_start, sch.finish as schedule_finish, 
        sh.id as shift_id, st.name as shift_name, sh.ordinal as shift_ordinal, 
        st.start as shift_start, st.finish - st.start as shift_duration
        FROM alarms a
        LEFT JOIN shifts sh ON sh.id = a.shift_id
        LEFT JOIN shift_types st ON st.id = sh.shift_type_id
        LEFT JOIN schedules sch ON sch.id = sh.schedule_id
        LEFT JOIN jobs j ON j.id = sch.job_id
    """
)
data class AlarmView(
    @Embedded(prefix = "alarm_")
    val alarm: AlarmItem,
    @Embedded(prefix = "job_")
    val job: JobItem,
    @Embedded(prefix = "schedule_")
    val schedule: ScheduleForAlarm,
    @Embedded(prefix = "shift_")
    val shift: ShiftForAlarm
)

@Parcelize
data class AlarmParcelable(
    val alarm: AlarmItem? = null,
    val job: JobItem? = null,
    val schedule: ScheduleForAlarm? = null,
    val shift: ShiftForAlarm? = null
) : Parcelable