package ru.mulledwine.shiftsredesigned.data.local.models

import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import ru.mulledwine.shiftsredesigned.R
import ru.mulledwine.shiftsredesigned.data.local.PrefManager

sealed class SettingsItem {

    companion object {
        const val VIEW_TYPE_ELEMENT = 0
        const val VIEW_TYPE_GROUP = 1
    }

    abstract val viewType: Int
    abstract fun getId(): String

    data class Element(
        val id: Int,
        val title: String,
        val description: String,
        val hasSwitch: Boolean,
        val isSwitchChecked: Boolean
    ) : SettingsItem() {
        override val viewType: Int = VIEW_TYPE_ELEMENT
        override fun getId(): String = id.toString()
    }

    data class Group(
        val title: String
    ) : SettingsItem() {
        override val viewType: Int = VIEW_TYPE_GROUP
        override fun getId(): String = title
    }
}

sealed class SettingsRes {

    abstract fun toSettingsItem(context: Context): SettingsItem

    data class Element(
        @IdRes val idRes: Int,
        @StringRes val titleRes: Int,
        @StringRes val descriptionRes: Int,
        val hasSwitch: Boolean = false
    ) : SettingsRes() {
        override fun toSettingsItem(context: Context): SettingsItem {

            val isSwitchChecked = if (hasSwitch) {
                when (idRes) {
                    // R.id.settings_export -> PrefManager.isFabCentered
                    else -> false
                }
            } else false

            return SettingsItem.Element(
                id = idRes,
                title = context.getString(titleRes),
                description = context.getString(descriptionRes),
                hasSwitch = hasSwitch,
                isSwitchChecked = isSwitchChecked
            )
        }
    }

    data class Group(
        @StringRes val titleRes: Int
    ) : SettingsRes() {
        override fun toSettingsItem(context: Context): SettingsItem {
            return SettingsItem.Group(
                title = context.getString(titleRes)
            )
        }
    }
}