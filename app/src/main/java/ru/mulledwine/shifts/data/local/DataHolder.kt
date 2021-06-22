package ru.mulledwine.shifts.data.local

import android.content.Context
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.data.local.models.ColorItem
import ru.mulledwine.shifts.data.local.models.ColorItemRes
import ru.mulledwine.shifts.data.local.models.SettingsItem

import ru.mulledwine.shifts.data.local.models.SettingsRes
import ru.mulledwine.shifts.ui.main.HintItemRes
import ru.mulledwine.shifts.ui.main.MainItem

object DataHolder {
    private val settings = listOf(
        SettingsRes.Group(R.string.settings_group_main),
        SettingsRes.Element(
            idRes = R.id.settings_holidays,
            titleRes = R.string.settings_title_holidays,
            descriptionRes = R.string.settings_description_holidays,
            hasSwitch = true
        ),
        SettingsRes.Group(R.string.settings_group_transfer),
        SettingsRes.Element(
            idRes = R.id.settings_export,
            titleRes = R.string.settings_title_export,
            descriptionRes = R.string.settings_description_export,
            hasSwitch = false
        ),
        SettingsRes.Element(
            idRes = R.id.settings_import,
            titleRes = R.string.settings_title_import,
            descriptionRes = R.string.settings_description_import,
            hasSwitch = false
        ),
        SettingsRes.Element(
            idRes = R.id.settings_excel,
            titleRes = R.string.settings_title_excel,
            descriptionRes = R.string.settings_description_excel,
            hasSwitch = false
        ),
        SettingsRes.Group(R.string.settings_group_notifications),
        SettingsRes.Element(
            idRes = R.id.settings_vibration,
            titleRes = R.string.settings_title_vibration,
            descriptionRes = R.string.settings_description_vibration,
            hasSwitch = true
        ),
        SettingsRes.Element(
            idRes = R.id.settings_melody,
            titleRes = R.string.settings_title_melody,
            descriptionRes = R.string.settings_description_melody,
            hasSwitch = false
        )
    )

    private val colors = listOf(
        ColorItemRes(colorRes = R.color.sizzling_red, nameRes = R.string.sizzling_red),
        ColorItemRes(colorRes = R.color.free_speech_blue, nameRes = R.string.free_speech_blue),
        ColorItemRes(colorRes = R.color.spiro_disco_ball, nameRes = R.string.spiro_disco_ball),
        ColorItemRes(colorRes = R.color.jade_dust, nameRes = R.string.jade_dust),
        ColorItemRes(colorRes = R.color.green_teal, nameRes = R.string.green_teal),
        ColorItemRes(colorRes = R.color.chrome_yellow, nameRes = R.string.chrome_yellow),
        ColorItemRes(colorRes = R.color.vibrant_yellow, nameRes = R.string.vibrant_yellow),
        ColorItemRes(colorRes = R.color.red_orange, nameRes = R.string.red_orange),
        ColorItemRes(colorRes = R.color.london_square, nameRes = R.string.london_square),
        ColorItemRes(colorRes = R.color.black_pearl, nameRes = R.string.black_pearl)
    )

    private val hints = listOf(
        HintItemRes(1, R.string.hint_item_long_click)
    )

    fun getSettings(context: Context): List<SettingsItem> {
        return settings.map {
            it.toSettingsItem(context)
        }
    }

    fun getColors(context: Context): List<ColorItem> {
        return colors.map {
            it.toColorItem(context)
        }
    }

    fun getHints(context: Context): List<MainItem.Hint> {
        return hints.map {
            it.toHintItem(context)
        }
    }

}