package ru.mulledwine.shiftsredesigned.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.*
import androidx.viewpager2.widget.ViewPager2
import ru.mulledwine.shiftsredesigned.R
import kotlin.math.abs

// https://github.com/nshmura/RecyclerTabLayout

class RecyclerTabLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attributeSet, defStyleAttr) {

    companion object {
        private const val DEFAULT_SCROLL_DURATION: Long = 200
        private const val DEFAULT_POSITION_THRESHOLD = 0.6f
        private const val POSITION_THRESHOLD_ALLOWABLE = 0.001f

        private const val TAG = "M_RecyclerTabLayout"
    }

    private lateinit var tabSelectedIndicator: Drawable
    private var tabSelectedIndicatorColor: Int = Color.TRANSPARENT
    private var indicatorHorizontalPadding: Int = 0

    private var tabOnScreenLimit: Int = 0

    private val linearLayoutManager: LinearLayoutManager
    private var viewPager: ViewPager2? = null
    private var adapter: Adapter<*>? = null

    private var indicatorPosition: Int = 0
    private var indicatorGap: Int = 0
    private var indicatorScroll: Int = 0
    private var oldPosition: Int = 0
    private var oldScrollOffset: Int = 0
    private var oldPositionOffset: Float = 0f
    private var positionThreshold: Float = 0f
    private var requestScrollToTab: Boolean = false

    init {
        setWillNotDraw(false)
        if (attributeSet != null) getAttributes(context, attributeSet)
        linearLayoutManager = LinearLayoutManager(context).apply { orientation = HORIZONTAL }
        layoutManager = linearLayoutManager
        itemAnimator = null
        positionThreshold = DEFAULT_POSITION_THRESHOLD
    }

    fun setCurrentItem(position: Int, smoothScroll: Boolean) {
        viewPager?.let {
            it.setCurrentItem(position, smoothScroll)
            scrollToTab(it.currentItem)
            return
        }
        if (smoothScroll && position != indicatorPosition) {
            startAnimation(position)
        } else {
            scrollToTab(position)
        }
    }

    private fun getAttributes(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerTabLayout)

        tabSelectedIndicator = a.getDrawable(
            R.styleable.RecyclerTabLayout_rtl_tabIndicatorDrawable
        ) ?: GradientDrawable()

        indicatorHorizontalPadding = a.getDimensionPixelSize(
            R.styleable.RecyclerTabLayout_rtl_tabIndicatorHeight,
            0
        )

        setSelectedIndicatorHeight(
            a.getDimensionPixelSize(R.styleable.RecyclerTabLayout_rtl_tabIndicatorHeight, -1)
        )

        tabOnScreenLimit = a.getInteger(
            R.styleable.RecyclerTabLayout_rtl_tabOnScreenLimit, 0
        )
        a.recycle()
    }

    private fun setSelectedIndicatorHeight(height: Int) {
        val bounds = tabSelectedIndicator.bounds
        tabSelectedIndicator.setBounds(bounds.left, 0, bounds.right, height)
        requestLayout()
    }

    private fun startAnimation(position: Int) {
        Log.d(TAG, "startAnimation")
        var distance = 1f
        val view: View? = linearLayoutManager.findViewByPosition(position)
        if (view != null) {
            // val currentX = view.x + view.measuredWidth / 2f
            // val centerX = measuredWidth / 2f
            val currentX = view.x
            val centerX = 0f
            distance = abs(centerX - currentX) / view.measuredWidth
        }
        val animator: ValueAnimator
        animator = if (position < indicatorPosition) {
            ValueAnimator.ofFloat(distance, 0f)
        } else {
            ValueAnimator.ofFloat(-distance, 0f)
        }
        animator.duration = DEFAULT_SCROLL_DURATION
        animator.addUpdateListener { animation ->
            scrollToTab(
                position,
                animation.animatedValue as Float,
                true
            )
        }
        animator.start()
    }

    private fun scrollToTab(position: Int) {
        scrollToTab(position, 0f, false)

        adapter?.let {
            val currentPosition = it.currentIndicatorPosition
            it.currentIndicatorPosition = position
            updateItems(currentPosition, position)
        }

    }

    private fun scrollToTab(position: Int, positionOffset: Float, fitIndicator: Boolean) {

        var scrollOffset = 0
        val selectedView: View? = linearLayoutManager.findViewByPosition(position)
        val nextView: View? = linearLayoutManager.findViewByPosition(position + 1)
        if (selectedView != null) {
            if (nextView != null) {
                val distance = (selectedView.measuredWidth + nextView.measuredWidth) / 2f
                val dx = distance * positionOffset
                scrollOffset = -dx.toInt()
                if (position == 0) {
                    val gap = ((nextView.measuredWidth - selectedView.measuredWidth) / 2).toFloat()
                    indicatorGap = (gap * positionOffset).toInt()
                    indicatorScroll = ((selectedView.measuredWidth + gap) * positionOffset).toInt()
                } else {
                    val gap = ((nextView.measuredWidth - selectedView.measuredWidth) / 2).toFloat()
                    indicatorGap = (gap * positionOffset).toInt()
                    indicatorScroll = dx.toInt()
                }
            } else {
                scrollOffset = 0
                indicatorScroll = 0
                indicatorGap = 0
            }
            if (fitIndicator) {
                indicatorScroll = 0
                indicatorGap = 0
            }
        } else {
            requestScrollToTab = true
        }
        updateCurrentIndicatorPosition(
            position,
            positionOffset - oldPositionOffset,
            positionOffset
        )
        indicatorPosition = position
        stopScroll()

        val first: Int = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
        val last: Int = linearLayoutManager.findLastCompletelyVisibleItemPosition()
        val isCompletelyVisible = position in first..last

        if (position != oldPosition || scrollOffset != oldScrollOffset || !isCompletelyVisible) {
            linearLayoutManager.scrollToPositionWithOffset(position, scrollOffset)
        }
        if (tabSelectedIndicator.bounds.height() > 0) {
            invalidate()
        }
        oldPosition = position
        oldScrollOffset = scrollOffset
        oldPositionOffset = positionOffset
    }

    private fun updateCurrentIndicatorPosition(position: Int, dx: Float, positionOffset: Float) {

        adapter?.let { adapter ->
            var indicatorPosition = -1
            if (dx > 0 && positionOffset >= positionThreshold - POSITION_THRESHOLD_ALLOWABLE) {
                indicatorPosition = position + 1
            } else if (dx < 0 && positionOffset <= 1 - positionThreshold + POSITION_THRESHOLD_ALLOWABLE) {
                indicatorPosition = position
            }
            if (indicatorPosition >= 0 && indicatorPosition != adapter.currentIndicatorPosition) {
                val currentPosition = adapter.currentIndicatorPosition
                adapter.currentIndicatorPosition = indicatorPosition
                updateItems(currentPosition, indicatorPosition)
            }
        }

    }

    private fun updateItems(vararg position: Int) {
        position.forEach {
            adapter?.notifyItemChanged(it, Adapter.RENDER_SELECTION_PAYLOAD)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val view: View? = linearLayoutManager.findViewByPosition(indicatorPosition)
        if (view == null) {
            if (requestScrollToTab) {
                requestScrollToTab = false
                scrollToTab(viewPager!!.currentItem)
            }
            return
        }
        requestScrollToTab = false

        val left: Int
        val right: Int
        if (isLayoutRtl()) {
            left = view.left - indicatorScroll - indicatorGap + indicatorHorizontalPadding
            right = view.right - indicatorScroll + indicatorGap - indicatorHorizontalPadding
        } else {
            left = view.left + indicatorScroll - indicatorGap + indicatorHorizontalPadding
            right = view.right + indicatorScroll + indicatorGap - indicatorHorizontalPadding
        }

        val indicatorTop: Int = view.height - tabSelectedIndicator.bounds.height()
        val indicatorBottom = view.height

        tabSelectedIndicator.setBounds(
            left, indicatorTop, right, indicatorBottom
        )

        var indicator = tabSelectedIndicator

        if (tabSelectedIndicatorColor != Color.TRANSPARENT) {
            indicator = DrawableCompat.wrap(indicator)
            indicator.setTint(tabSelectedIndicatorColor)
            indicator.setTintMode(PorterDuff.Mode.SRC_IN)
        }

        indicator.draw(canvas)

    }

    private fun isLayoutRtl(): Boolean {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    private inner class ViewPagerOnPageChangeCallback : ViewPager2.OnPageChangeCallback() {
        private var scrollState = 0

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            scrollToTab(position, positionOffset, false)
        }

        override fun onPageScrollStateChanged(state: Int) {
            scrollState = state
        }

        override fun onPageSelected(position: Int) {
            if (scrollState == ViewPager2.SCROLL_STATE_IDLE) {
                if (indicatorPosition != position) {
                    scrollToTab(position)
                }
            }
        }
    }

    fun setUpWithAdapter(adapter: Adapter<*>) {
        this@RecyclerTabLayout.adapter = adapter
        adapter.tabOnScreenLimit = tabOnScreenLimit
        viewPager = adapter.viewPager.also {
            requireNotNull(it.adapter) { "ViewPager does not have a PagerAdapter set" }
            it.registerOnPageChangeCallback(
                ViewPagerOnPageChangeCallback()
            )
            setAdapter(adapter)
            scrollToTab(it.currentItem)
        }
    }

    abstract class Adapter<T>(
        diffCallback: DiffUtil.ItemCallback<T>,
        internal val viewPager: ViewPager2
    ) : ListAdapter<T, Adapter<T>.ViewHolder>(diffCallback) {

        companion object {
            const val RENDER_SELECTION_PAYLOAD = "RENDER_SELECTION_PAYLOAD"
        }

        var currentIndicatorPosition = 0
        var tabOnScreenLimit: Int = 0

        abstract inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            init {
                itemView.setOnClickListener {
                    val pos = bindingAdapterPosition
                    if (pos != NO_POSITION) {
                        viewPager.setCurrentItem(pos, true)
                    }
                }
            }

            abstract fun bind(item: T, position: Int)
            abstract fun renderSelection(isSelected: Boolean)
        }

    }

}