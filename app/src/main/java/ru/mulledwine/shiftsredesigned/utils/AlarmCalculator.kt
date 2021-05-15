package ru.mulledwine.shiftsredesigned.utils

import android.util.Log
import ru.mulledwine.shiftsredesigned.data.local.models.ClockTime
import ru.mulledwine.shiftsredesigned.data.local.models.TimeUnits
import ru.mulledwine.shiftsredesigned.extensions.*

class AlarmCalculator() {

    companion object {
        private const val TAG = "M_AlarmCalculator"
    }

    var errorMessage: String? = null
        private set

    var alarmTime: Long = 0L
        private set

    class Builder {

        private var isCyclic: Boolean = false
        private var scheduleStart: Long = 0L
        private var shiftOrdinal: Int = 0
        private var shiftsCount: Int = 0
        private lateinit var shiftClock: ClockTime
        private lateinit var alarmClock: ClockTime

        fun setIsCyclic(isCyclic: Boolean): Builder {
            this.isCyclic = isCyclic
            return this
        }

        fun setScheduleStart(scheduleStart: Long): Builder {
            this.scheduleStart = scheduleStart
            return this
        }

        fun setShiftOrdinal(shiftOrdinal: Int): Builder {
            this.shiftOrdinal = shiftOrdinal
            return this
        }

        fun setShiftsCount(shiftsCount: Int): Builder {
            this.shiftsCount = shiftsCount
            return this
        }

        fun setShiftClockTime(shiftClock: ClockTime): Builder {
            this.shiftClock = shiftClock
            return this
        }

        fun setAlarmClockTime(alarmClock: ClockTime): Builder {
            this.alarmClock = alarmClock
            return this
        }

        fun build(): AlarmCalculator {

            val result = AlarmCalculator()

            val now = Utils.getTime()

            val shiftStart = getShiftStart()
            if (shiftStart - now < 0L) {
                return result.apply {
                    errorMessage = "Начало смены уже прошло"
                }
            }

            val alarmTime = getAlarmTime(shiftStart)
            val delta = alarmTime - now
            if (delta < 0L) {
                return result.apply {
                    errorMessage = "Время будильника уже прошло"
                }
            }

            result.alarmTime = alarmTime
            return result
        }

        private fun getShiftStart(): Long {
            return if (isCyclic) getShiftStartCyclic()
            else getShiftStartNonCyclic()
        }

        private fun getShiftStartNonCyclic(): Long {
            val shiftDelta = TimeUnits.DAY.value * (shiftOrdinal - 1)

            return scheduleStart + shiftDelta + shiftClock.toLong()
        }

        private fun getShiftStartCyclic(): Long {

            val firstShiftStart = getShiftStartNonCyclic()

            val now = Utils.getTime()
            val cycleDuration = shiftsCount * TimeUnits.DAY.value
            // количество прошедших полных циклов от смены до смены
            val cyclesPassed = (now - firstShiftStart) / cycleDuration
            return firstShiftStart + (cyclesPassed + 1) * cycleDuration // ближайшее начало смены
        }

        private fun getAlarmTime(shiftStart: Long): Long {

            val alarmDelta = shiftClock - alarmClock
            var time = shiftStart - alarmDelta.toLong()

            // для цикличных графиков:
            // если смена ещё не началась (будет сегодня вечером)
            // а время будильника уже прошло (было утром)
            if (isCyclic) {
                val now = Utils.getTime()
                val cycleDuration = shiftsCount * TimeUnits.DAY.value
                if (time < now) {
                    time += cycleDuration
                }
            }

            Log.d(TAG, "alarm ${time.toDate()}")
            return time
        }

    }
}