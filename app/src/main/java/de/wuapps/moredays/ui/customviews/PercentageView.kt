package de.wuapps.moredays.ui.customviews

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import de.wuapps.moredays.R
import de.wuapps.moredays.ui.home.GoalEntry
import kotlin.math.abs


// based on https://github.com/turkergoksu/PercentageView
class PercentageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val START_ANGLE_OF_PERCENTAGE_BAR = 135F
    private val PERCENTAGE_TOTAL_ANGLE = 270F

    private var rectF: RectF = RectF()
    private var centerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val startCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val endCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var percentage = 33F
    private var textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        textSize = 50.0f
    }
    private var hasRoundEnds = true
    private var _label: String = ":)"
    private var percentageWidth: Float = 50f

    private var goalEntries: List<GoalEntry> = ArrayList()
    private var desiredTotal = 10

    var label: String
        get() = _label
        set(value) {
            _label = value
        }


    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PercentageView,
            0,
            0
        )
        typedArray.apply {
            // PERCENTAGE WIDTH
            percentageWidth =
                abs(getFloat(R.styleable.PercentageView_percentageWidth, 50f))

            // CENTER COLOR
            centerPaint.color = getColor(
                R.styleable.PercentageView_centerColor,
                Color.WHITE
            )

            progressBackgroundPaint.color = getColor(
                R.styleable.PercentageView_progressBackgroundColor,
                Color.GRAY
            )
            startCirclePaint.color = progressBackgroundPaint.color
            endCirclePaint.color = progressBackgroundPaint.color

            textPaint.color = getColor(
                R.styleable.PercentageView_textColor,
                Color.BLACK
            )

            textPaint.textSize = resources.getDimension(R.dimen.textsize_percentage)
            textPaint.textSize =
                getDimensionPixelSize(R.styleable.PercentageView_textSize, 32).toFloat()

            hasRoundEnds = getBoolean(R.styleable.PercentageView_roundEnds,true )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF.left = 0F
        rectF.top = 0F
        rectF.right = width.coerceAtMost(height).toFloat()
        rectF.bottom = width.coerceAtMost(height).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val centerX = (measuredWidth / 2).toFloat()
        val centerY = (measuredHeight / 2).toFloat()
        val radius = centerX.coerceAtMost(centerY)

        // Percentage bar background
        canvas?.drawArc(
            rectF,
            START_ANGLE_OF_PERCENTAGE_BAR,
            PERCENTAGE_TOTAL_ANGLE,
            true,
            progressBackgroundPaint
        )

        // Percentage bar
        var startAngle = START_ANGLE_OF_PERCENTAGE_BAR
        var isStart = true
        for (item in goalEntries) {
            if (item.getDesiredValue() > 0 && item.getCurrentValue() > 0) {
                var angle = getAngleToPercentage(item.getCurrentValue() * 100F / desiredTotal)
                progressPaint.color = item.getColor()
                canvas?.drawArc(
                    rectF,
                    startAngle,
                    angle,
                    true,
                    progressPaint
                )
                startAngle += angle
                if (isStart && angle > 0) { //first entry
                    startCirclePaint.color = item.getColor()
                    isStart = false
                }
                if (startAngle - START_ANGLE_OF_PERCENTAGE_BAR >=PERCENTAGE_TOTAL_ANGLE) //100% reached
                    endCirclePaint.color = item.getColor()
            }
        }

        if (hasRoundEnds){
            val fillCircleRadius = percentageWidth / 2
            val diffOuterCircleInnerCircle = radius - fillCircleRadius
            //start circle
            val angle = START_ANGLE_OF_PERCENTAGE_BAR.toDouble()
            //y should be +, but due to the angle it's -- ...
            canvas?.drawCircle(
                rectF!!.centerX() - diffOuterCircleInnerCircle *
                        Math.sin(Math.toRadians(angle)).toFloat(),
                rectF!!.centerY() - diffOuterCircleInnerCircle *
                        Math.cos(Math.toRadians(angle)).toFloat(),
                fillCircleRadius,
                startCirclePaint
            )
            //end circle
            canvas?.drawCircle(
                rectF!!.centerX() + diffOuterCircleInnerCircle *
                        Math.sin(Math.toRadians(angle)).toFloat(),
                rectF!!.centerY() - diffOuterCircleInnerCircle *
                        Math.cos(Math.toRadians(angle)).toFloat(),
                fillCircleRadius,
                endCirclePaint
            )
        }

        // Center circle of percentage bar -- to have a width instead of a full circle
        canvas?.drawCircle(
            rectF.centerX(),
            rectF.centerY(),
            radius - percentageWidth,
            centerPaint
        )

        // Draw Text
        val textSizeWidth =
            textPaint.measureText(label)
        canvas?.drawText(
            label,
            rectF.centerX() - textSizeWidth / 2,
            rectF.centerY() + textPaint.textSize / 2,
            textPaint
        )
    }


    private fun setPercentage() {
        this.percentage = percentage
    }

    private fun getAngleToPercentage(percentage: Float): Float {
        return (percentage * PERCENTAGE_TOTAL_ANGLE) / 100
    }

    fun setCirclesData(circleData: List<GoalEntry>) {
        goalEntries = circleData
        calcPercentage()
        this.requestLayout() //recalculate needed width/height
    }

    private fun calcPercentage() {
        val sum = goalEntries.sumOf<GoalEntry>({ goalEntry -> goalEntry.getCurrentValue() })
        desiredTotal = goalEntries.sumOf<GoalEntry>({ goalEntry -> goalEntry.getDesiredValue() })
        percentage = sum * 100F / desiredTotal
        if (desiredTotal < sum)
            desiredTotal = sum //do not make the circle bigger than full
        setPercentage()
        if (sum > 0)
            _label = String.format("%.1f %%", percentage)
    }
}