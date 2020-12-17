package ru.mulledwine.shiftsredesigned.data.local

import androidx.room.*
import ru.mulledwine.shiftsredesigned.App
import ru.mulledwine.shiftsredesigned.BuildConfig
import ru.mulledwine.shiftsredesigned.data.local.dao.SchedulesDao
import ru.mulledwine.shiftsredesigned.data.local.dao.ShiftTypesDao
import ru.mulledwine.shiftsredesigned.data.local.dao.ShiftsDao
import ru.mulledwine.shiftsredesigned.data.local.dao.VacationsDao
import ru.mulledwine.shiftsredesigned.data.local.entities.*
import ru.th1ngshappen.shifts.data.local.dao.*

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
        Schedule::class,
        ShiftType::class,
        Shift::class,
        Vacation::class
    ],
    version = AppDb.DATABASE_VERSION,
    exportSchema = true,
    views = [ShiftsWithTypeView::class]
)

@TypeConverters(CalendarConverter::class, ShiftTimeConverter::class)
abstract class AppDb : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db"
        const val DATABASE_VERSION = 1
    }

    abstract fun daysDao(): DaysDao
    abstract fun schedulesDao(): SchedulesDao
    abstract fun shiftsDao(): ShiftsDao
    abstract fun shiftTypesDao(): ShiftTypesDao
    abstract fun vacationsDao(): VacationsDao
}