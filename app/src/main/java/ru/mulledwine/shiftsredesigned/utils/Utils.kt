package ru.mulledwine.shiftsredesigned.utils

import android.graphics.Color
import androidx.annotation.ColorInt
import ru.mulledwine.shiftsredesigned.extensions.daysFrom
import java.util.*

object Utils {

    private const val TAG = "M_Utils"

    fun getCalendarInstance(): Calendar =
        Calendar.getInstance(Locale("ru", "RU"))

    fun getCalendarInstance(timeInMillis: Long): Calendar {
        val instance = getCalendarInstance()
        instance.timeInMillis = timeInMillis
        return instance
    }

    fun getTime(): Long = getCalendarInstance().timeInMillis

    fun getDuration(startTime: Long, finishTime: Long): Int {
        val start = getCalendarInstance(startTime)
        val finish = getCalendarInstance(finishTime)
        return finish.daysFrom(start)
    }

    private val rgb = 0..255

    @ColorInt
    fun getRandomColor(): Int {
        val r = rgb.random()
        val g = rgb.random()
        val b = rgb.random()

        return Color.rgb(r, g, b)
    }

}