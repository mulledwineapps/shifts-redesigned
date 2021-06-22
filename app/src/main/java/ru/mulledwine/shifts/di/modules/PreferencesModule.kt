package ru.mulledwine.shifts.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.mulledwine.shifts.data.local.PrefManager
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object PreferencesModule {
    @Singleton
    @Provides
    // PrefManager получаем от ActivityComponent
    // @ApplicationContext запровайдит сюда application context
    fun providePrefManager(@ApplicationContext context: Context): PrefManager =
        PrefManager(context)
}