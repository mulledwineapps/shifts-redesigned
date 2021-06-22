package ru.mulledwine.shifts.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.mulledwine.shifts.App
import ru.mulledwine.shifts.BuildConfig
import ru.mulledwine.shifts.data.local.dao.*
import ru.mulledwine.shifts.data.local.entities.*

@Database(
    entities = [
        Day::class,
        Job::class,
        Schedule::class,
        ShiftType::class,
        Shift::class,
        Vacation::class,
        Alarm::class
    ],
    version = AppDb.DATABASE_VERSION,
    exportSchema = true,
    views = [ShiftsWithTypeView::class, AlarmView::class]
)
@TypeConverters(ClockTimeConverter::class)
abstract class AppDb : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db"
        const val DATABASE_VERSION = 1
    }

    abstract fun daysDao(): DaysDao
    abstract fun jobsDao(): JobsDao
    abstract fun schedulesDao(): SchedulesDao
    abstract fun shiftsDao(): ShiftsDao
    abstract fun shiftTypesDao(): ShiftTypesDao
    abstract fun vacationsDao(): VacationsDao
    abstract fun alarmsDao(): AlarmsDao
}