package ru.mulledwine.shifts.viewmodels

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mulledwine.shifts.data.local.CalendarGenerator
import ru.mulledwine.shifts.data.local.PrefManager
import ru.mulledwine.shifts.data.local.entities.ShiftType
import ru.mulledwine.shifts.repositories.SplashRepository
import ru.mulledwine.shifts.viewmodels.base.IViewModelState
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val repository: SplashRepository,
    private val calendarGenerator: CalendarGenerator,
    private val preferences: PrefManager
) : BaseViewModel<SplashState>(handle, SplashState()) {

    companion object {
        private const val TAG = "M_SplashViewModel"
    }

    init {
        // preferences.resetHints()
        launchSafely {
            generateCalendar()
            updateState { it.copy(isAppReady = true) }
        }
    }

    private suspend fun generateCalendar() {
        if (!preferences.isCalendarGenerated) {
            calendarGenerator.generate()
            generateTestData()
            preferences.isCalendarGenerated = true
        }
    }

    private suspend fun generateTestData() {

        val shiftTypes = ShiftType.generate()
//        val jobs = Job.generate()
//
//        val twoTwoPattern = shiftTypes.dropLast(1)
//        val twoThreePattern = shiftTypes.dropLast(1) + shiftTypes[3]
//        val patterns = setOf(twoTwoPattern, twoThreePattern)
//
//        val schedules = jobs.map { Schedule.generate(it.id!!) }
//
//        var shiftCounter = 0
//
//        val shifts = schedules.flatMap { schedule ->
//
//            var ordinal = 1
//            val pattern = patterns.random()
//
//            pattern.map { shiftType ->
//                Shift(
//                    id = shiftCounter++,
//                    scheduleId = schedule.id!!,
//                    shiftTypeId = shiftType.id!!,
//                    ordinal = ordinal++
//                )
//            }
//        }

        repository.insertShiftTypesToDb(shiftTypes)
//        repository.insertJobsToDb(jobs)
//        repository.insertSchedulesToDb(schedules)
//        repository.insertShiftsToDb(shifts)

    }

}

data class SplashState(
    val isAppReady: Boolean = false
) : IViewModelState