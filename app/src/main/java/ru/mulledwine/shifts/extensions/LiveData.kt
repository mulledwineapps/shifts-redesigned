package ru.mulledwine.shifts.extensions

import androidx.lifecycle.MutableLiveData

fun <T> mutableLiveData(defaultValue: T? = null): MutableLiveData<T> {
    val data = MutableLiveData<T>()

    defaultValue?.let {
        data.value = it
    }

    return data
}