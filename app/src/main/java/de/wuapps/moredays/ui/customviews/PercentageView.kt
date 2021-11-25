package de.wuapps.moredays.ui.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import de.wuapps.moredays.R
import de.wuapps.moredays.ui.home.GoalEntry
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin


class PercentageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // CONSTANTS
    private val START_ANGLE_OF_PERCENTAGE_BAR = 135F
    private val PERCENTAGE_TOTAL_ANGLE = 270F

    // BAR
    private var rectF: RectF? = null
    private var centerPaint = Paint()
    private var progressPaint = Paint()
    private var progressBackgroundPaint = Paint()
    private var isSmooth = true

    // TEXT
    private var percentage = 33F
    private var percentageTextPaint = TextPaint()
    private var _label: String = "los"

    private var percentageWidth: Float = 50f
    private var typedArray: TypedArray? = null

    private var goalEntries: List<GoalEntry> = ArrayList()
    private var desiredTotal = 10

    /**
     * The text to draw
     */
    var label: String
        get() = _label
        set(value) {
            _label = value
        }


    init {
        typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PercentageView,
            0,
            0
        )
        initAttributes()
    }

    private fun initAttributes() {
        typedArray?.apply {
            // FINAL PERCENTAGE VALUE
            percentage = abs(getInt(R.styleable.PercentageView_percentage, 0)).toFloat()

            // PERCENTAGE WIDTH
            percentageWidth =
                abs(getFloat(R.styleable.PercentageView_percentageWidth, 50f))

            // CENTER COLOR
            centerPaint.color = getColor(
                R.styleable.PercentageView_centerColor,
                Color.WHITE
            )
            // TODO: 13-Nov-20 get rootview background color as default value

            // PROGRESS COLOR
            progressPaint.color = getColor(
                R.styleable.PercentageView_progressColor,
                Color.RED
            )

            // PROGRESS BACKGROUND COLOR
            progressBackgroundPaint.color = getColor(
                R.styleable.PercentageView_progressBackgroundColor,
                Color.GRAY
            )

            // PERCENTAGE TEXT COLOR
            // Default color value should be same with progressPaint
            percentageTextPaint.color = getColor(
                R.styleable.PercentageView_textColor,
                Color.BLACK
            )

            // PERCENTAGE TEXT SIZE
            percentageTextPaint.textSize = resources.getDimension(R.dimen.textsize_percentage)
            percentageTextPaint.textSize =
                getDimensionPixelSize(R.styleable.PercentageView_textSize, 32).toFloat()

            // PERCENTAGE TEXT FONT
            val fontId = getResourceId(R.styleable.PercentageView_android_fontFamily, 0)
            if (fontId != 0) {
                percentageTextPaint.typeface = ResourcesCompat.getFont(context, fontId)
            }

        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        rectF = RectF(
            0f,
            0f,
            width.coerceAtMost(height).toFloat(),
            width.coerceAtMost(height).toFloat()
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val centerX = (measuredWidth / 2).toFloat()
        val centerY = (measuredHeight / 2).toFloat()
        val radius = centerX.coerceAtMost(centerY)

        // Percentage bar background
        canvas?.drawArc(
            rectF!!,
            START_ANGLE_OF_PERCENTAGE_BAR,
            PERCENTAGE_TOTAL_ANGLE,
            true,
            progressBackgroundPaint
        )

        // Percentage bar
        var startAngle = START_ANGLE_OF_PERCENTAGE_BAR
        for (item in goalEntries) {
            if (item.getDesiredValue() > 0 && item.getCurrentValue() > 0) {
                var angle = getAngleToPercentage(item.getCurrentValue() * 100F / desiredTotal)
                progressPaint.color = item.getColor()
                canvas?.drawArc(
                    rectF!!,
                    startAngle,
                    angle,
                    true,
                    progressPaint
                )
                Log.d("circle", String.format("%s %.1f %.1f", item.getName(), startAngle, angle))
                Log.d("circle, id", item.getId().toString())
                startAngle += angle
            }
        }


        // Center circle of percentage bar -- to have a width instead of a full circle
        canvas?.drawCircle(
            rectF!!.centerX(),
            rectF!!.centerY(),
            radius - percentageWidth,
            centerPaint //use background color of widget
        )

        // Draw Text
        val textSizeWidth =
            percentageTextPaint.measureText(label)
        canvas?.drawText(
            label,
            rectF!!.centerX() - textSizeWidth / 2,
            rectF!!.centerY() + percentageTextPaint.textSize / 2,
            percentageTextPaint
        )
    }


    private fun setPercentage() {
        this.percentage = percentage
    }

    private fun getAngleToPercentage(percentage: Float): Float
    {
        return (percentage * PERCENTAGE_TOTAL_ANGLE) / 100
    }

    fun setCirclesData(circleData: List<GoalEntry>) {
        goalEntries = circleData
        calcPercentage()
        this.requestLayout() //recalculate needed width/height
    }

    private fun calcPercentage() {
        val sum = goalEntries.sumBy { goalEntry -> goalEntry.getCurrentValue() }
        desiredTotal = goalEntries.sumBy { goalEntry -> goalEntry.getDesiredValue() }
        percentage = sum * 100F /desiredTotal
        if (desiredTotal < sum)
            desiredTotal = sum //do not make the circle bigger than full
        setPercentage()
        if (sum > 0)
            _label = String.format("%.1f %%", percentage)
    }
}