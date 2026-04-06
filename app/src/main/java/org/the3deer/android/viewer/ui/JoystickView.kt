package org.the3deer.android.viewer.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min
import kotlin.math.sqrt

class JoystickView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 50
        style = Paint.Style.FILL
    }

    private val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 150
        style = Paint.Style.FILL
    }

    private var centerX = 0f
    private var centerY = 0f
    private var baseRadius = 0f
    private var hatRadius = 0f

    private var touchX = 0f
    private var touchY = 0f

    private var listener: ((Float, Float) -> Unit)? = null

    fun setJoystickListener(listener: (Float, Float) -> Unit) {
        this.listener = listener
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        baseRadius = min(w, h) / 3f
        hatRadius = baseRadius / 2f
        
        touchX = centerX
        touchY = centerY
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(centerX, centerY, baseRadius, outerPaint)
        canvas.drawCircle(touchX, touchY, hatRadius, innerPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                performClick()
                updateTouch(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                updateTouch(event.x, event.y)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchX = centerX
                touchY = centerY
                sendUpdate()
                invalidate()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun updateTouch(x: Float, y: Float) {
        val dx = x - centerX
        val dy = y - centerY
        val distance = sqrt(dx * dx + dy * dy)

        if (distance < baseRadius) {
            touchX = x
            touchY = y
        } else {
            val ratio = baseRadius / distance
            touchX = centerX + dx * ratio
            touchY = centerY + dy * ratio
        }

        sendUpdate()
        invalidate()
    }

    private fun sendUpdate() {
        val dx = (touchX - centerX) / baseRadius
        val dy = (touchY - centerY) / baseRadius
        listener?.invoke(dx, -dy) // Invert Y for standard 3D coordinate system
    }
}
