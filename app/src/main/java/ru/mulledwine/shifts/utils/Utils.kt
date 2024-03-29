package ru.mulledwine.shifts.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.annotation.ColorInt
import ru.mulledwine.shifts.data.local.entities.AlarmParcelable
import ru.mulledwine.shifts.extensions.daysFrom
import ru.mulledwine.shifts.ui.RootActivity
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

    fun cancelAlarm(context: Context, alarmId: Int) {
        val flags = PendingIntent.FLAG_NO_CREATE
        val pendingIntent = getPendingIntent(context, alarmId, flags) ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun setAlarm(context: Context, alarm: AlarmParcelable, time: Long) {
        val pendingIntent = getPendingIntent(context, alarm.id, 0, alarm)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // val info = AlarmManager.AlarmClockInfo(time, pendingIntent)
        // alarmManager.setAlarmClock(info, pendingIntent)
        // TODO запросить разрешения на использование батареи в спящем режиме
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    private fun getPendingIntent(
        context: Context,
        requestCode: Int,
        flags: Int,
        alarm: AlarmParcelable? = null
    ): PendingIntent? {
        val intent = Intent(context, RootActivity::class.java)
        if (alarm != null) intent.putExtra(RootActivity.ALARM_PARAM, alarm)
        return PendingIntent.getActivity(context, requestCode, intent, flags)
    }

}