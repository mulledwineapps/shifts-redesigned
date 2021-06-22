package ru.mulledwine.shifts.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView

class SimpleTextWatcher(private val callback: (String) -> Unit) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        callback.invoke(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, st: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}

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