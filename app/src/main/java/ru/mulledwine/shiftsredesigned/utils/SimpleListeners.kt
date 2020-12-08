package ru.mulledwine.shiftsredesigned.utils

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView

class SimpleOnEditorActionListener(
    private val onDoneCallback: () -> Unit
) :
    TextView.OnEditorActionListener {

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_DONE) {
            onDoneCallback.invoke()
            true
        } else false
    }
}