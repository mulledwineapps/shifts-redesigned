package ru.mulledwine.shiftsredesigned.viewmodels

import androidx.lifecycle.SavedStateHandle
import ru.mulledwine.shiftsredesigned.data.local.CalendarGenerator
import ru.mulledwine.shiftsredesigned.data.local.PrefManager
import ru.mulledwine.shiftsredesigned.data.local.entities.Schedule
import ru.mulledwine.shiftsredesigned.data.local.entities.Shift
import ru.mulledwine.shiftsredesigned.data.local.entities.ShiftType
import ru.mulledwine.shiftsredesigned.repositories.SplashRepository
import ru.mulledwine.shiftsredesigned.viewmodels.base.IViewModelState

class SplashViewModel(handle: SavedStateHandle) :
    BaseViewModel<SplashState>(handle, SplashState()) {

    companion object {
        private const val TAG = "M_SplashViewModel"
    }

    private val repository: SplashRepository = SplashRepository

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

        val shiftTypes = ShiftType.generateAll()
        val schedules = Schedule.generateAll()

        var shiftId = 0

        val shifts = schedules.flatMap { schedule ->

            var ordinal = 1

            val types = when (schedule.name) {
                "2 через 2" -> shiftTypes.dropLast(1)
                "2 через 3" -> shiftTypes.dropLast(1) + shiftTypes[3]
                else -> {
                    val patternLength = (2..3).random()
                    shiftTypes.shuffled().take(patternLength)
                        .flatMap { shiftType -> List((1..3).random()) { shiftType } }
                }
            }

            types.map { shiftType ->
                Shift(
                    id = shiftId++,
                    scheduleId = schedule.id!!,
                    shiftTypeId = shiftType.id!!,
                    ordinal = ordinal++
                )
            }

        }

        repository.insertSchedulesToDb(schedules)
        repository.insertShiftTypesToDb(shiftTypes)
        repository.insertShiftsToDb(shifts)

    }

}

data class SplashState(
    val isAppReady: Boolean = false
) : IViewModelState