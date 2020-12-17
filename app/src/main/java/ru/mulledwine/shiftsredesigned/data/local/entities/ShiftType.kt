package ru.mulledwine.shiftsredesigned.data.local.entities

//import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import ru.mulledwine.shiftsredesigned.data.local.models.ShiftTime
import ru.mulledwine.shiftsredesigned.utils.Utils

@Parcelize
@Entity(tableName = "shift_types")
data class ShiftType(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val color: Int,
    @ColumnInfo(name = "is_full_day")
    val isFullDay: Boolean,
    val start: ShiftTime = ShiftTime(0, 0),
    val finish: ShiftTime = ShiftTime(0, 0)
) : Parcelable {

    companion object {
        private var counter = 0
        private val daysOff = setOf("Отсыпной", "Выходной", "Отгул")
        private val names = setOf("Дневная", "Ночная") + daysOff

        fun generateAll(): List<ShiftType> {
            return (names).map {
                val isFullDay = daysOff.contains(it)
                if (isFullDay) {
                    ShiftType(
                        id = counter++,
                        name = it,
                        isFullDay = isFullDay,
                        color = Utils.getRandomColor(),
                    )
                } else {
                    ShiftType(
                        id = counter++,
                        name = it,
                        isFullDay = isFullDay,
                        start = ShiftTime.random(),
                        finish = ShiftTime.random(),
                        color = Utils.getRandomColor(),
                    )
                }
            }
        }
    }

}