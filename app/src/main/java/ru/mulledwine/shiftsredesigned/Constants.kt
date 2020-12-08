package ru.mulledwine.shiftsredesigned

import android.graphics.Typeface
import androidx.core.content.ContextCompat
import ru.mulledwine.shiftsredesigned.extensions.dpToIntPx
import ru.mulledwine.shiftsredesigned.extensions.dpToPx
import ru.mulledwine.shiftsredesigned.extensions.getDayId
import ru.mulledwine.shiftsredesigned.extensions.zeroTime
import ru.mulledwine.shiftsredesigned.utils.Utils
import java.util.*

object Constants {

    // TODO check if it's really zero
    val today: Calendar = Utils.getCalendarInstance().apply { zeroTime() }
    val todayId = today.getDayId()

    val alabasterColor = ContextCompat.getColor(App.applicationContext(), R.color.alabaster)

    val dp4 = App.applicationContext().dpToIntPx(4)
    val dp8 = App.applicationContext().dpToIntPx(8)
    val dp16 = App.applicationContext().dpToIntPx(16)
    val dp40 = App.applicationContext().dpToIntPx(40)
    val dp48 = App.applicationContext().dpToPx(48)
    val listItemHeight72 =
        App.applicationContext().resources.getDimension(R.dimen.list_item_height_72).toInt()

    val typefaceNormal: Typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    val typefaceMediumNormal: Typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)

    val weekDays = App.applicationContext()
        .resources.getStringArray(R.array.days_of_week_array).toList()
}