package ru.mulledwine.shiftsredesigned.data.local.models

import android.content.Context
import ru.mulledwine.shiftsredesigned.R

enum class Months(val resName: Int) {
    JANUARY(R.string.january),
    FEBRUARY(R.string.february),
    MARCH(R.string.march),
    APRIL(R.string.april),
    MAY(R.string.may),
    JUNE(R.string.june),
    JULY(R.string.july),
    AUGUST(R.string.august),
    SEPTEMBER(R.string.september),
    OCTOBER(R.string.october),
    NOVEMBER(R.string.november),
    DECEMBER(R.string.december);

    fun getName(context: Context) = context.resources.getString(resName)

}