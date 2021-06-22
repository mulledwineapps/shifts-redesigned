package ru.mulledwine.shifts.di.modules

import android.app.Activity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import ru.mulledwine.shifts.ui.RootActivity

@InstallIn(ActivityComponent::class)
@Module
class ActivityModule {
    @Provides
    fun provideActivity(activity: Activity): RootActivity = activity as RootActivity
}