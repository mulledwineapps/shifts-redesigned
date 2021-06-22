package ru.mulledwine.shifts.data.local.entities

import androidx.room.*

@Entity(
    tableName = "shifts",
    foreignKeys = [
        ForeignKey(
            entity = ShiftType::class,
            parentColumns = ["id"],
            childColumns = ["shift_type_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Schedule::class,
            parentColumns = ["id"],
            childColumns = ["schedule_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Shift(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "schedule_id", index = true)
    val scheduleId: Int,
    @ColumnInfo(name = "shift_type_id", index = true)
    val shiftTypeId: Int,
    val ordinal: Int, // порядковый номер смены в графике с 1
)

data class ShiftWithType(
    @Embedded val shift: Shift,
    @Relation(
        parentColumn = "shift_type_id",
        entityColumn = "id"
    )
    val type: ShiftType
)

@DatabaseView(
    """
        SELECT * FROM shifts
    """
)
data class ShiftsWithTypeView(
    @Embedded val value: ShiftWithType
)