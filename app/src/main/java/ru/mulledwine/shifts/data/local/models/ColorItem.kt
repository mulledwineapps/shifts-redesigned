package ru.mulledwine.shifts.data.local.models

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.util.*

data class ColorItem(
    @ColorInt val color: Int,
    val name: String,
    val hex: String
)

data class ColorItemRes(
    @ColorRes val colorRes: Int,
    @StringRes val nameRes: Int
) {
    fun toColorItem(context: Context): ColorItem {
        val color = ContextCompat.getColor(context, colorRes)
        return ColorItem(
            color = color,
            name = context.getString(nameRes),
            hex = "#" + Integer.toHexString(color).removeRange(0..1).toUpperCase(Locale.ROOT)
        )
    }
}