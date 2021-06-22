package ru.mulledwine.shifts.ui.custom

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView

class DividerItemDecoration(
    private val divider: Drawable,
    private val shouldDrawDividerBelow: (itemPosition: Int) -> Boolean
) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        val childCount = parent.childCount
        val width = parent.width
        for (childViewIndex in 0 until childCount) {
            val view = parent.getChildAt(childViewIndex)
            if (shouldDrawDividerBelow(childViewIndex)) {
                val top = view.y.toInt() + view.height
                divider.setBounds(0, top, width, top + divider.intrinsicHeight)
                divider.draw(c)
            }
        }

        super.onDrawOver(c, parent, state)
    }
}