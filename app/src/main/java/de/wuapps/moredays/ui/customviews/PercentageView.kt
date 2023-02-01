package de.wuapps.moredays.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import de.wuapps.moredays.R
import de.wuapps.moredays.ui.home.GoalEntry
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


// based on https://github.com/turkergoksu/PercentageView and https://tomkubasik.medium.com/enhance-your-android-app-with-custom-view-ad344d28b501
class PercentageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val TAG = "PercentageView"

    private val START_ANGLE_OF_PERCENTAGE_BAR = 135F
    private val PERCENTAGE_TOTAL_ANGLE = 270F

    private var rectF: RectF = RectF()
    private var centerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val startCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val endCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var percentage = 0F
    private var sum = 0
    private var textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 50.0f
    }
    private var hasRoundEnds = true
    private var _label: String = ":)"
    private var percentageWidth: Float = 50f
    private var _usePercentageStyle = true
    private var goalEntries: List<GoalEntry> = ArrayList()
    private var desiredTotal = 10

    var label: String
        get() = _label
        set(value) {
            _label = value
        }

    // provide public/setter/getter for data binding
    var usePercentageStyle: Boolean
        get() = _usePercentageStyle
        set(value) {
            _usePercentageStyle = value
        }

    init {
        Log.d(TAG, "ini")
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
            usePercentageStyle = getBoolean(R.styleable.PercentageView_usePercentageStyle,true )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val squareSize = min(width, height).toFloat()
        var marginLeft = 0F
        var marginTop = 0F
        if (width > height) { //center horizontally
            marginLeft = (width - height)/2F
        }
        else {
            marginTop = (height - width)/2F
        }
        rectF.left = marginLeft
        rectF.top = marginTop
        rectF.right = marginLeft + squareSize
        rectF.bottom = marginTop + squareSize
        Log.d(TAG, "onSizeChanged - height:"+height + ", width:"+width+", right:"+rectF.right + ", bottom"+rectF.bottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null || goalEntries.isEmpty())
            return
        Log.d(TAG, "start onDraw")
        val centerX = (width / 2).toFloat()
        val centerY = (height / 2).toFloat()
        val radius = centerX.coerceAtMost(centerY)
        Log.d(TAG, "x:"+centerX + "y:"+centerY + ", radius:"+radius + ", right:"+rectF.right + ", bottom"+rectF.bottom)
        // Percentage bar background
        canvas.drawArc(
            rectF,
            START_ANGLE_OF_PERCENTAGE_BAR,
            PERCENTAGE_TOTAL_ANGLE,
            true,
            progressBackgroundPaint
        )

        // Percentage bar
        var startAngle = START_ANGLE_OF_PERCENTAGE_BAR
        var isStart = true
        val pointsFullCircle = if (sum < desiredTotal) desiredTotal else sum
        for (item in goalEntries) {
            if (item.getDesiredValue() > 0 && item.getCurrentValue() > 0) {
                val angle = getAngleToPercentage(item.getCurrentValue() * 100F / pointsFullCircle)
                progressPaint.color = item.getColor()
                canvas.drawArc(
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
                if (angle > 0 && percentage >= 100) //100% reached
                    endCirclePaint.color = item.getColor()
            }
        }

        if (hasRoundEnds){
            val fillCircleRadius = percentageWidth / 2
            val diffOuterCircleInnerCircle = radius - fillCircleRadius
            //start circle
            val angle = START_ANGLE_OF_PERCENTAGE_BAR.toDouble()
            //y should be +, but due to the obtuse angle it's -- ...
            canvas.drawCircle(
                rectF.centerX() - diffOuterCircleInnerCircle *
                        sin(Math.toRadians(angle)).toFloat(),
                rectF.centerY() - diffOuterCircleInnerCircle *
                        cos(Math.toRadians(angle)).toFloat(),
                fillCircleRadius,
                startCirclePaint
            )
            //end circle
            canvas.drawCircle(
                rectF.centerX() + diffOuterCircleInnerCircle *
                        sin(Math.toRadians(angle)).toFloat(),
                rectF.centerY() - diffOuterCircleInnerCircle *
                        cos(Math.toRadians(angle)).toFloat(),
                fillCircleRadius,
                endCirclePaint
            )
        }

        // Center circle of percentage bar -- to have a width instead of a full circle
        canvas.drawCircle(
            rectF.centerX(),
            rectF.centerY(),
            radius - percentageWidth,
            centerPaint
        )

        // Draw Text
        val textSizeWidth =
            textPaint.measureText(label)
        canvas.drawText(
            label,
            rectF.centerX() - textSizeWidth / 2,
            rectF.centerY() + textPaint.textSize / 2,
            textPaint
        )
        Log.d(TAG, "end onDraw")
    }

    private fun getAngleToPercentage(percentage: Float): Float {
        return (percentage * PERCENTAGE_TOTAL_ANGLE) / 100
    }

    fun setCirclesData(circleData: List<GoalEntry>) {
        Log.d(TAG, "setcirclesDAta")
        goalEntries = circleData
        calcPercentage()
        startCirclePaint.color = progressBackgroundPaint.color
        endCirclePaint.color = progressBackgroundPaint.color
        invalidate() //force redraw
    }

    private fun calcPercentage() {
        sum = goalEntries.sumOf { goalEntry -> goalEntry.getCurrentValue() }
        desiredTotal = goalEntries.sumOf { goalEntry -> goalEntry.getDesiredValue() }
        percentage = sum * 100F / desiredTotal
        //if (desiredTotal < sum)
        //    desiredTotal = sum //do not make the circle bigger than full
        if (sum > 0) {
            if (usePercentageStyle)
                _label = String.format("%.1f %%", percentage)
            else
                _label = String.format("%d / %d", sum, desiredTotal)
        }
    }
}