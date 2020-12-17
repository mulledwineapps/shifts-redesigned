package ru.mulledwine.shiftsredesigned.extensions

fun Int.getScheduleGenitive(): String {
    return when (this) {
        in 11..14 -> "$this выбранных графиков"
        else -> when (this % 10) {
            1 -> "$this выбранный график"
            in 2..4 -> "$this выбранных графика"
            else -> "$this выбранных графиков"
        }
    }
}

fun Int.getVacationGenitive(): String {
    return when (this) {
        in 11..14 -> "$this выбранных отпуска"
        else -> when (this % 10) {
            1 -> "$this выбранный отпуск"
            in 2..4 -> "$this выбранных отпуска"
            else -> "$this выбранных отпусков"
        }
    }
}

fun Int.getDaysGenitive(): String {
    return when (this) {
        in 11..14 -> "$this дней"
        else -> when (this % 10) {
            1 -> "$this день"
            in 2..4 -> "$this дня"
            else -> "$this дней"
        }
    }
}