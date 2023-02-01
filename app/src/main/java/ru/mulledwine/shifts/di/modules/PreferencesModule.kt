package ru.mulledwine.shifts.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.mulledwine.shifts.data.local.PrefManager
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PreferencesModule {
    @Singleton
    @Provides
    // PrefManager получаем от ActivityComponent
    // @ApplicationContext запровайдит сюда application context
    fun providePrefManager(@ApplicationContext context: Context): PrefManager =
        PrefManager(context)
}