package ru.mulledwine.shiftsredesigned.extensions

import ru.mulledwine.shiftsredesigned.Constants
import ru.mulledwine.shiftsredesigned.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

val Calendar.year get() = this.get(Calendar.YEAR)
val Calendar.month get() = this.get(Calendar.MONTH) // начиная с нуля
val Calendar.date get() = this.get(Calendar.DATE)
val Calendar.dayOfWeek get() = this.get(Calendar.DAY_OF_WEEK)

val Calendar.hour get() = this.get(Calendar.HOUR_OF_DAY)
val Calendar.minute get() = this.get(Calendar.MINUTE)

val Calendar.daysFromWeekStart
    get(): Int = dayOfWeek -
            if (dayOfWeek < firstDayOfWeek) firstDayOfWeek - 7
            else firstDayOfWeek

fun Calendar.getDayId(): String {
    // pad необходим, чтобы 11_0_2020 не было равно 1_10_2020
    val date = "$date".padStart(2, '0')
    val month = "$month".padStart(2, '0')
    return "$date$month$year"
}

fun Calendar.zeroTime() {
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
}

fun Calendar.setWithZeroTime(year: Int, month: Int, date: Int) {
    set(year, month, date, 0, 0, 0)
}

fun Calendar.moveToNextDay() = add(Calendar.DATE, 1)

fun Calendar.formatAndCapitalize(): String {
    val monthFormat = SimpleDateFormat("MMMM", Locale("ru"))
    val month: String = monthFormat.format(this.time).capitalize(Locale("ru"))
    val year = if (this.year == Constants.today.year) "" else " $year"
    return "$date $month$year"
}

fun Calendar.formatAndCapitalize(formatString: String): String {
    val formatted = SimpleDateFormat(formatString, Locale("ru")).format(this.time)
    return formatted.split(" ").joinToString(" ") { it.capitalize(Locale("ru")) }
}

fun Calendar.format(): String {
    val monthFormat = SimpleDateFormat("MMMM", Locale("ru"))
    val month: String = monthFormat.format(this.time)
    val year = if (this.year == Constants.today.year) "" else " $year"
    return "$date $month$year"
}

fun Calendar.daysFrom(from: Calendar): Int {
    val start = from.getCopyWithZeroTime()
    val end = this.getCopyWithZeroTime()
    end.add(Calendar.DATE, 1)

    val startTime = start.timeInMillis
    val endTime = end.timeInMillis

    val periodSeconds: Long = (endTime - startTime) / 1000
    val elapsedDays = periodSeconds / 60 / 60 / 24

    return elapsedDays.toInt()
}

private fun Calendar.getCopyWithZeroTime(): Calendar {
    val instance = Utils.getCalendarInstance(this.timeInMillis)
    instance.zeroTime()
    return instance
}
