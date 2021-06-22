package ru.mulledwine.shifts.data.local.entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import ru.mulledwine.shifts.data.local.models.ClockTime
import ru.mulledwine.shifts.utils.Utils

@Parcelize
@Entity(tableName = "shift_types")
data class ShiftType(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val color: Int,
    @ColumnInfo(name = "is_day_off")
    val isDayOff: Boolean,
    @ColumnInfo(name = "is_full_day")
    val isFullDay: Boolean,
    val start: ClockTime = ClockTime(0, 0),
    val finish: ClockTime = ClockTime(0, 0)
) : Parcelable {

    companion object {
        private var counter = 0
        private val daysOff = setOf("Отсыпной", "Выходной", "Отгул")
        private val names = setOf("Дневная", "Ночная") + daysOff

        fun generate(): List<ShiftType> {
            return (names).map {
                val isFullDay = daysOff.contains(it)
                if (isFullDay) {
                    ShiftType(
                        id = counter++,
                        name = it,
                        isDayOff = isFullDay,
                        isFullDay = isFullDay,
                        color = Utils.getRandomColor(),
                    )
                } else {
                    ShiftType(
                        id = counter++,
                        name = it,
                        isDayOff = isFullDay,
                        isFullDay = isFullDay,
                        start = ClockTime.random(),
                        finish = ClockTime.random(),
                        color = Utils.getRandomColor(),
                    )
                }
            }
        }
    }

}