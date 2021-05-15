package ru.mulledwine.shiftsredesigned.extensions

fun Int.getJobGenitive(): String {
    return when (this) {
        in 11..14 -> "$this выбранных работ"
        else -> when (this % 10) {
            1 -> "$this выбранную работу"
            in 2..4 -> "$this выбранные работы"
            else -> "$this выбранных работ"
        }
    }
}
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

fun Int.getAlarmGenitive(): String {
    return when (this) {
        in 11..14 -> "$this выбранных будильников"
        else -> when (this % 10) {
            1 -> "$this выбранный будильник"
            in 2..4 -> "$this выбранных будильника"
            else -> "$this выбранных будильников"
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

fun Int.getHoursGenitive(): String {
    return when (this) {
        in 11..14 -> "$this часов"
        else -> when (this % 10) {
            1 -> "$this час"
            in 2..4 -> "$this часа"
            else -> "$this часов"
        }
    }
}

fun Int.getMinutesGenitive(): String {
    return when (this) {
        in 11..14 -> "$this минут"
        else -> when (this % 10) {
            1 -> "$this минута"
            in 2..4 -> "$this минуты"
            else -> "$this минут"
        }
    }
}