package ru.mulledwine.shiftsredesigned.data.local

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ru.mulledwine.shiftsredesigned.App
import ru.mulledwine.shiftsredesigned.data.delegates.PrefDelegate

object PrefManager {

    internal val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    var isCalendarGenerated by PrefDelegate(false)

}