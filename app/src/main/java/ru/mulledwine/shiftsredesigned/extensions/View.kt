package ru.mulledwine.shiftsredesigned.extensions

import android.widget.EditText
import android.widget.RelativeLayout

fun EditText.getTrimmedString() = text.trim().toString()

fun RelativeLayout.LayoutParams.addRules(vararg rules: Int) {
    rules.forEach {
        this.addRule(it)
    }
}