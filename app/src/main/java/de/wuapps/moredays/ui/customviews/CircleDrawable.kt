package de.wuapps.moredays.ui.customviews

import android.graphics.*
import android.graphics.drawable.Drawable

class CircleDrawable(
    private var color: Int,
    private val percent: Float,
    private val strokeWidth: Int
) :
    Drawable() {
    private val paint: Paint
    private val rectF: RectF
    fun setColor(color: Int) {
        this.color = color
        paint.color = color
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        val bounds = bounds
        val size = (Math.min(
            Math.abs(bounds.top - bounds.bottom),
            Math.abs(bounds.right - bounds.left)
        ) - strokeWidth - 10) / 2 //10 ist margin
        rectF[(canvas.width / 2 - size).toFloat(), (canvas.height / 2 - size).toFloat(), (canvas.width / 2 + size).toFloat()] =
            (canvas.height / 2 + size).toFloat()
        paint.color = color
        paint.strokeWidth = strokeWidth.toFloat()
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.BUTT
        paint.style = Paint.Style.STROKE
        val startAngle = -90
        val angle = 360.0f * percent / 100.0f
        canvas.drawArc(rectF, startAngle.toFloat(), angle, false, paint)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    init {
        paint = Paint()
        paint.color = color
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        rectF = RectF()
    }
}