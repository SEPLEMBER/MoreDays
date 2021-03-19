package de.wuapps.moredays.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import de.wuapps.moredays.ui.home.GoalEntry
import java.lang.Integer.min

/**
 * TODO: document your custom view class.
 */
class CirclesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var _circlesLabel: String = ""
    private lateinit var textPaint: TextPaint
    private val rect = RectF()

    // Paint styles used for rendering are initialized here. This
    // is a performance optimization, since onDraw() is called
    // for every screen refresh.
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        textSize = 50.0f
    }
    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.BUTT
        style = Paint.Style.STROKE
    }
    private var goalEntries: List<GoalEntry> = ArrayList()

    private val margin = 2
    private val minRadius = 70
    private val circleWidth = 50
    private var viewSize = 50

    /**
     * The text to draw
     */
    var circlesLabel: String
        get() = _circlesLabel
        set(value) {
            _circlesLabel = value
        }

    init {
        isClickable = true
    }

    override fun performClick(): Boolean {
        // Give default click listeners priority and perform accessibility/autofill events.
        // Also calls onClickListener() to handle further subclass customizations.
        if (super.performClick()) return true
        //handle click
        return true
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * the view was just added to the view hierarchy, it is called with the old
     * values of 0. The code determines the drawing bounds for the custom view.
     *
     * @param width    Current width of this view.
     * @param height    Current height of this view.
     * @param oldWidth Old width of this view.
     * @param oldHeight Old height of this view.
     */
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        // Calculate the radius from the smaller of the width and height.
        viewSize = min(width, height) / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var radius = minRadius

        for (item in goalEntries) {
            if (item.getDesiredValue() > 0) {
                var angle = item.getCurrentValue() * 360F / item.getDesiredValue()
                val countCircles = angle.toInt() / 360
                for (i in 0 until countCircles) {
                    drawProgress(canvas, item.getColor(), radius, circleWidth, 360)
                    radius += circleWidth + margin
                }
                angle = angle - 360 * countCircles
                if (angle > 0) {
                    drawProgress(canvas, item.getColor(), radius, circleWidth, angle.toInt())
                    radius += circleWidth + margin
                }
            }
        }
        drawLabel(canvas)
        invalidate()
    }

    private fun drawProgress(canvas: Canvas, color: Int, radius: Int, width: Int, angle: Int) {
        val size = viewSize
        rect.left = canvas.width / 2F - radius - width
        rect.top = size - radius - width * 1F
        rect.right = canvas.width / 2F + radius + width
        rect.bottom = size + radius + width * 1F
        paintCircle.color = color
        paintCircle.strokeWidth = width.toFloat()
        canvas.drawArc(rect, -90f, angle.toFloat(), false, paintCircle)
    }

    private fun drawLabel(canvas: Canvas) {
        if (circlesLabel != null && circlesLabel!!.trim { it <= ' ' }.isNotEmpty()) {
            val yPos = (canvas.height / 2 - (paintText.descent() + paintText.ascent()) / 2).toInt()
            val xPos = canvas.width / 2
            canvas.drawText(circlesLabel!!, xPos.toFloat(), yPos.toFloat(), paintText)
        }
    }

    fun getCirclesData(): List<GoalEntry> {
        return goalEntries
    }

    fun setCirclesData(circleData: List<GoalEntry>) {
        this.goalEntries = circleData
        calcPercentage()
        this.requestLayout() //recalculate needed width/height
    }

    private fun calcPercentage() {
        val sum = goalEntries.sumBy { goalEntry -> goalEntry.getCurrentValue() }
        val desired = goalEntries.sumBy { goalEntry -> goalEntry.getDesiredValue() }
        if (sum > 0)
            _circlesLabel = String.format("%.1f %%", sum * 100F /desired)
    }
}
