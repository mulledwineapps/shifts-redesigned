package ru.mulledwine.shifts

import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import ru.mulledwine.shifts.extensions.dpToIntPx
import ru.mulledwine.shifts.extensions.getDayId
import ru.mulledwine.shifts.extensions.year
import ru.mulledwine.shifts.extensions.zeroTime
import ru.mulledwine.shifts.utils.Utils
import java.util.*

object Constants {

    val today: Calendar = Utils.getCalendarInstance().apply { zeroTime() }
    val todayId = today.getDayId()

    val firstYear = today.year - 2
    val lastYear = today.year + 2

    @ColorInt val brightGray = ContextCompat.getColor(App.applicationContext(), R.color.bright_gray)
    @ColorInt val brightGray54 = ContextCompat.getColor(App.applicationContext(), R.color.bright_gray_54)
    @ColorInt val selectionColor = ContextCompat.getColor(App.applicationContext(), R.color.color_selection)

    val dp8 = App.applicationContext().dpToIntPx(8)
    val dp16 = App.applicationContext().dpToIntPx(16)
    val dp40 = App.applicationContext().dpToIntPx(40)

    val typefaceNormal: Typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    val typefaceMediumNormal: Typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)

    val weekDays = App.applicationContext()
        .resources.getStringArray(R.array.days_of_week_array).toList()
}