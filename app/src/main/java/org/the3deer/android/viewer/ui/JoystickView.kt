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

    // outerPaint: used for the background circle of the joystick. 
    // Low alpha (50) keeps it subtle on all backgrounds.
    private val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 50
        style = Paint.Style.FILL
    }

    // innerPaint: used for the movable thumb/stick. 
    // Higher alpha (180) and a shadow layer provide visibility and depth.
    private val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 180
        style = Paint.Style.FILL
        // Shadow adds a 3D effect and improves visibility on white backgrounds
        setShadowLayer(5f, 0f, 2f, Color.argb(100, 0, 0, 0))
    }

    // borderPaint: used to outline the joystick components.
    // Essential for visibility when the fragment background is also white.
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        alpha = 80
        style = Paint.Style.STROKE
        strokeWidth = 2f
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
        val availableWidth = w - paddingLeft - paddingRight
        val availableHeight = h - paddingTop - paddingBottom

        centerX = paddingLeft + availableWidth / 2f
        centerY = paddingTop + availableHeight / 2f
        baseRadius = min(availableWidth, availableHeight) / 3f
        hatRadius = baseRadius / 2f
        
        touchX = centerX
        touchY = centerY
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Outer circle
        canvas.drawCircle(centerX, centerY, baseRadius, outerPaint)
        canvas.drawCircle(centerX, centerY, baseRadius, borderPaint)

        // Inner circle (hat)
        canvas.drawCircle(touchX, touchY, hatRadius, innerPaint)
        canvas.drawCircle(touchX, touchY, hatRadius, borderPaint)
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
