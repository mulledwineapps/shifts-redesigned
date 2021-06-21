package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import ru.mulledwine.shiftsredesigned.data.local.CalendarGenerator
import ru.mulledwine.shiftsredesigned.data.local.PrefManager
import ru.mulledwine.shiftsredesigned.data.local.entities.Job
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.Shift
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.repositories.SplashRepository
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

class SplashViewModel @ViewModelInject constructor(
    @Assisted handle: SavedStateHandle,
    private val repository: SplashRepository
) : BaseViewModel<SplashState>(handle, SplashState()) {

    companion object {
        private const val TAG = "M_SplashViewModel"
    }

    init {
        launchSafely {
            generateCalendar()
            updateState { it.copy(isAppReady = true) }
        }
    }

    private suspend fun generateCalendar() {
        if (!PrefManager.isCalendarGenerated) {
            CalendarGenerator.generate()
            generateTestData()
            PrefManager.isCalendarGenerated = true
        }
    }

    private suspend fun generateTestData() {

        val shiftTypes = ShiftType.generate()
        val jobs = Job.generate()

        val twoTwoPattern = shiftTypes.dropLast(1)
        val twoThreePattern = shiftTypes.dropLast(1) + shiftTypes[3]
        val patterns = setOf(twoTwoPattern, twoThreePattern)

        val schedules = jobs.map { Schedule.generate(it.id!!) }

        var shiftCounter = 0

        val shifts = schedules.flatMap { schedule ->

            var ordinal = 1
            val pattern = patterns.random()

            pattern.map { shiftType ->
                Shift(
                    id = shiftCounter++,
                    scheduleId = schedule.id!!,
                    shiftTypeId = shiftType.id!!,
                    ordinal = ordinal++
                )
            }
        }

        repository.insertShiftTypesToDb(shiftTypes)
        repository.insertJobsToDb(jobs)
        repository.insertSchedulesToDb(schedules)
        repository.insertShiftsToDb(shifts)

    }

}

data class SplashState(
    val isAppReady: Boolean = false
) : IViewModelState