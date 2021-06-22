package ru.mulledwine.shifts.ui.custom

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import ru.mulledwine.shifts.R
import ru.mulledwine.shifts.extensions.addRules
import ru.mulledwine.shifts.extensions.dpToIntPx


class SwipeButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attributeSet, defStyleAttr) {

    private var swipeRightListener: (() -> Unit)? = null
    private var swipeLeftListener: (() -> Unit)? = null

    private val button: ImageView
    private var initialX = 0f
    private val leftText: TextView
    private val rightText: TextView

    private val textMargin = context.dpToIntPx(24)
    private val slidingDrawable = ContextCompat.getDrawable(context, R.drawable.sliding_button)
    private val rect: RectF = RectF()
    private val circleRect: RectF = RectF()
    private val paintLeft = Paint().apply { color = Color.MAGENTA }
    private val paintRight = Paint().apply { color = Color.CYAN }

    init {
        leftText = TextView(context).apply {
            setTextColor(Color.WHITE)
            text = "Отложить"
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                addRules(ALIGN_PARENT_START, CENTER_VERTICAL)
                marginStart = textMargin
            }
        }
        addView(leftText)

        rightText = TextView(context).apply {
            setTextColor(Color.WHITE)
            text = "Выключить"
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                addRules(ALIGN_PARENT_END, CENTER_VERTICAL)
                marginEnd = textMargin
            }
        }
        addView(rightText)

        button = SquareImageViewH(context).apply {
            background = slidingDrawable
            layoutParams = LayoutParams(0, MATCH_PARENT).apply {
                addRule(CENTER_IN_PARENT)
            }
        }
        addView(button)

        setOnTouchListener(getButtonTouchListener())
    }

    override fun dispatchDraw(canvas: Canvas) {

        val half = width / 2f
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val radius = h / 2

        circleRect.set(0f, 0f, h, h)
        canvas.drawOval(circleRect, paintLeft)
        rect.set(radius, 0f, half, h)
        canvas.drawRect(rect, paintLeft)

        rect.set(half, 0f, w - radius, h)
        canvas.drawRect(rect, paintRight)
        circleRect.set(w - h, 0f, w, h)
        canvas.drawOval(circleRect, paintRight)

        super.dispatchDraw(canvas)
    }

    fun setSwipeRightListener(listener: () -> Unit) {
        swipeRightListener = listener
    }

    fun setSwipeLeftListener(listener: () -> Unit) {
        swipeLeftListener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun getButtonTouchListener(): OnTouchListener {
        return OnTouchListener { _, event ->
            return@OnTouchListener when (event.action) {
                MotionEvent.ACTION_DOWN -> true
                MotionEvent.ACTION_MOVE -> {
                    handleMovement(event.x)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    handleRelease()
                    true
                }
                else -> false
            }
        }
    }

    private fun handleRelease() {
        val closeToRight = button.x + button.width == width.toFloat()
        val closeToLeft = button.x == 0f
        when {
            closeToRight -> {
                swipeRightListener?.invoke()
            }
            closeToLeft -> {
                swipeLeftListener?.invoke()
            }
            else -> moveButtonBack()
        }
    }

    private fun moveButtonBack() {
        val animator = ValueAnimator.ofFloat(button.x, initialX)
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener {
            val x = animator.animatedValue as Float
            button.x = x
        }
        animator.duration = 200
        animator.start()
    }

    private fun handleMovement(x: Float) {
        if (initialX == 0f) initialX = button.x

        val halfButton = button.width / 2

        // x - позиция пойнтера на экране в пределах родительской вьюхи

        // двигает кнопку вправо
        if (x > initialX + halfButton && x <= width - halfButton) {
            button.x = x - halfButton
        }

        // двигает кнопку влево
        if (x < initialX + halfButton && x >= halfButton) {
            button.x = x - halfButton
        }

        // останавливает кнопку в правой части
        if (x > width - halfButton && button.x + button.width < width) {
            button.x = width - button.width.toFloat()
        }

        // останавливает кнопку в левой части
        if (x < halfButton && button.x > 0) {
            button.x = 0f
        }
    }

}