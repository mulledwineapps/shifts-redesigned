package ru.mulledwine.shiftsredesigned.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attributeSet, defStyleAttr) {

    var onSizeChangeListener: (() -> Unit)? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (oldh != 0) onSizeChangeListener?.invoke()
        super.onSizeChanged(w, h, oldw, oldh)
    }
}