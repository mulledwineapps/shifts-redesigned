package ru.mulledwine.shifts.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.mulledwine.shifts.data.local.AppDb
import ru.mulledwine.shifts.data.local.dao.*
import javax.inject.Singleton

// модуль провайдит базу данных и необходимые дао
// для каждого provide метода поставить аннотацию @Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DbModule {
    @Provides
    @Singleton
    fun provideAppDb(@ApplicationContext context: Context): AppDb = Room.databaseBuilder(
        context,
        AppDb::class.java,
        AppDb.DATABASE_NAME
    ).build()

    /*init {
        // use instead of changing database version when no need to save data
        App.applicationContext().deleteDatabase(AppDb.DATABASE_NAME)
        PrefManager.isCalendarGenerated = false // TODO временно
    }*/

    @Provides
    @Singleton
    fun provideAlarmsDao(db: AppDb): AlarmsDao = db.alarmsDao()

    @Provides
    @Singleton
    fun provideDaysDao(db: AppDb): DaysDao = db.daysDao()

    @Provides
    @Singleton
    fun provideJobsDao(db: AppDb): JobsDao = db.jobsDao()

    @Provides
    @Singleton
    fun provideSchedulesDao(db: AppDb): SchedulesDao = db.schedulesDao()

    @Provides
    @Singleton
    fun provideShiftsDao(db: AppDb): ShiftsDao = db.shiftsDao()

    @Provides
    @Singleton
    fun provideShiftTypesDao(db: AppDb): ShiftTypesDao = db.shiftTypesDao()

    @Provides
    @Singleton
    fun provideVacationsDao(db: AppDb): VacationsDao = db.vacationsDao()

}