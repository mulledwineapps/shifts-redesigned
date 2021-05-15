package ru.mulledwine.shiftsredesigned.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.mulledwine.shiftsredesigned.App
import ru.mulledwine.shiftsredesigned.BuildConfig
import ru.mulledwine.shiftsredesigned.data.local.dao.*
import ru.mulledwine.shiftsredesigned.data.local.entities.*

object DbManager {
    init {
        // use instead of changing database version when no need to save data
//        App.applicationContext().deleteDatabase(AppDb.DATABASE_NAME)
//        PrefManager.isCalendarGenerated = false // TODO временно
    }

    val db = Room.databaseBuilder(
        App.applicationContext(),
        AppDb::class.java,
        AppDb.DATABASE_NAME
    ).build()
}

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
    views = [ShiftsWithTypeView::class, AlarmView:: class]
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