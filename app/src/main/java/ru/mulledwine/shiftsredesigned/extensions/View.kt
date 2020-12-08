package ru.mulledwine.shiftsredesigned.extensions

import android.view.View
import android.widget.EditText

fun EditText.getTrimmedString() = text.trim().toString()