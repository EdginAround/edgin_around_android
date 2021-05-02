package com.edgin.around.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

const val TAG: String = "EdginAround"

/** Widget displaying a joystick with action buttons (wheels). */
class ActionRing(context: Context, attrs: AttributeSet) : View(context, attrs) {
    /** Point on the widget relatively to origin position of some action wheel. */
    data class Point(val x: Float, val y: Float) {
        constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())

        companion object {
            /** Calculates distance between two points. */
            fun distance(p1: Point, p2: Point): Float {
                val diffX = p1.x - p2.x
                val diffY = p1.y - p2.y
                return sqrt(diffX * diffX + diffY * diffY)
            }
        }

        /** Moves the `point` back to the circle described by the `radius` if it sticks out. */
        fun keepInRadius(point: Point, radius: Float): Point {
            val distance = Point.distance(this, point)
            if (radius < distance) {
                val m = radius / distance
                return Point(m * (point.x - this.x) + this.x, m * (point.y - this.y) + this.y)
            } else {
                return point
            }
        }
    }

    /** Describes position of a wheel (action button) */
    data class WheelPosition(
        /** Clock-wise angle measured from center-top axis. */
        val angle: Float,

        /** Number from `0.0` to `1.0` describing how far the wheel is from the center. */
        val magnitude: Float
    )

    /** Describes state of current touch gesture. */
    data class TouchState(var point: Point)

    /** Listener for wheel events. */
    interface WheelListener {
        fun onPositionChanged(position: WheelPosition)
        fun onFinished()
    }

    val DEFAULT_RING_RADIUS: Int = 300
    val DEFAULT_WHEEL_RADIUS: Int = 125
    val DEFAULT_PADDING_SIZE: Int = 10

    val DEFAULT_RING_STROKE_WIDTH: Int = 10
    val DEFAULT_RING_STROKE_COLOR: Int = 0x55000000.toInt()
    val DEFAULT_RING_FILL_COLOR: Int = 0x11000000.toInt()
    val DEFAULT_HUB_STROKE_WIDTH: Int = 10
    val DEFAULT_HUB_STROKE_COLOR: Int = 0x33000000.toInt()
    val DEFAULT_HUB_FILL_COLOR: Int = 0x00000000.toInt()
    val DEFAULT_WHEEL_STROKE_WIDTH: Int = 10
    val DEFAULT_WHEEL_STROKE_COLOR: Int = 0xEE000000.toInt()
    val DEFAULT_WHEEL_FILL_COLOR: Int = 0xBB000000.toInt()

    var mainRingRadius: Int = DEFAULT_RING_RADIUS
    var wheelRadius: Int = DEFAULT_WHEEL_RADIUS
    var viewWidth: Int = calcMinSize()
    var viewHeight: Int = calcMinSize()
    var paddingSize: Int = DEFAULT_PADDING_SIZE

    var ringStrokeWidth: Int = DEFAULT_RING_STROKE_WIDTH
    var ringStrokeColor: Int = DEFAULT_RING_STROKE_COLOR
    var ringFillColor: Int = DEFAULT_RING_FILL_COLOR
    var hubStrokeWidth: Int = DEFAULT_HUB_STROKE_WIDTH
    var hubStrokeColor: Int = DEFAULT_HUB_STROKE_COLOR
    var hubFillColor: Int = DEFAULT_HUB_FILL_COLOR
    var wheelStrokeWidth: Int = DEFAULT_WHEEL_STROKE_WIDTH
    var wheelStrokeColor: Int = DEFAULT_WHEEL_STROKE_COLOR
    var wheelFillColor: Int = DEFAULT_WHEEL_FILL_COLOR

    var touchState: TouchState? = null
    var listener: WheelListener? = null

    private val mainRingFillPaint = Paint().apply {
        isAntiAlias = false
        style = Paint.Style.FILL
        color = ringFillColor
    }

    private val mainRingStrokePaint = Paint().apply {
        isAntiAlias = false
        style = Paint.Style.STROKE
        color = ringStrokeColor
        strokeWidth = ringStrokeWidth.toFloat()
    }

    private val mainHubFillPaint = Paint().apply {
        isAntiAlias = false
        style = Paint.Style.FILL
        color = hubFillColor
    }

    private val mainHubStrokePaint = Paint().apply {
        isAntiAlias = false
        style = Paint.Style.STROKE
        color = hubStrokeColor
        strokeWidth = hubStrokeWidth.toFloat()
    }

    private val mainWheelFillPaint = Paint().apply {
        isAntiAlias = false
        style = Paint.Style.FILL
        color = wheelFillColor
    }

    private val mainWheelStrokePaint = Paint().apply {
        isAntiAlias = false
        style = Paint.Style.STROKE
        color = wheelStrokeColor
        strokeWidth = wheelStrokeWidth.toFloat()
    }

    /** Overrides `View.onSizeChanged`. */
    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldWidth: Int, oldHeight: Int) {
        viewWidth = newWidth
        viewHeight = newHeight
    }

    /** Overrides `View.onMeasure`. */
    override fun onMeasure(width: Int, height: Int) {
        val minSize: Int = calcMinSize()
        setMeasuredDimension(minSize, minSize)
    }

    /** Overrides `View.onDraw`. */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val ringPos = calcCenterPoint()
        val wheelPos = calcWheelPoint(ringPos)

        canvas.apply {
            drawCircle(ringPos.x, ringPos.y, mainRingRadius.toFloat(), mainRingFillPaint)
            drawCircle(ringPos.x, ringPos.y, mainRingRadius.toFloat(), mainRingStrokePaint)
            drawCircle(ringPos.x, ringPos.y, wheelRadius.toFloat(), mainHubFillPaint)
            drawCircle(ringPos.x, ringPos.y, wheelRadius.toFloat(), mainHubStrokePaint)
            drawCircle(wheelPos.x, wheelPos.y, wheelRadius.toFloat(), mainWheelFillPaint)
            drawCircle(wheelPos.x, wheelPos.y, wheelRadius.toFloat(), mainWheelStrokePaint)
        }
    }

    /** Overrides `View.onTouchEvent`. */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (touchState == null) {
            when (event.getActionMasked()) {
                MotionEvent.ACTION_DOWN -> {
                    // Start new touch
                    handleTouch(event.getX(), event.getY())
                }
                else -> {
                    // Ignore other touch events
                }
            }
        } else {
            when (event.getActionMasked()) {
                MotionEvent.ACTION_DOWN -> {
                    // Ignore new touches
                }
                MotionEvent.ACTION_MOVE -> {
                    handleTouch(event.getX(), event.getY())
                }
                MotionEvent.ACTION_UP -> {
                    handleTouch(event.getX(), event.getY())
                    stopTouch()
                }
                MotionEvent.ACTION_CANCEL -> {
                    stopTouch()
                }
                else -> {
                    Log.i(TAG, "Unhandled event: ${event.getActionMasked()}")
                }
            }
        }

        return true
    }

    /** Sets radius of the ring. */
    fun setRingRadius(radius: Int) {
        mainRingRadius = radius
    }

    /** Sets listener of events from the main wheel. */
    fun setWheelListener(wheelListener: WheelListener) {
        listener = wheelListener
    }

    /** Returns current position of the main wheel. */
    fun getWheelPosition(): WheelPosition {
        val state = touchState
        if (state != null) {
            val center = calcCenterPoint()
            val distance = Point.distance(center, state.point)
            val magnitude = maxOf(1.0f, distance / mainRingRadius)

            val x = state.point.x - center.x
            val y = state.point.y - center.y
            val angle = PI - atan2(x, y)

            return WheelPosition(angle.toFloat(), magnitude)
        } else {
            return WheelPosition(0.0f, 0.0f)
        }
    }

    /** Calculates the origin of the main wheel. */
    private fun calcCenterPoint(): Point {
        return Point(mainRingRadius + paddingSize, viewHeight - mainRingRadius - paddingSize)
    }

    /** Calculates the current position of the main wheel. */
    private fun calcWheelPoint(ringPoint: Point): Point {
        val state = touchState
        if (state != null) {
            return ringPoint.keepInRadius(state.point, (mainRingRadius - wheelRadius).toFloat())
        } else {
            return ringPoint
        }
    }

    /** Calculates the minimal size of the widget. */
    private fun calcMinSize(): Int {
        return 2 * (mainRingRadius + paddingSize)
    }

    /** Handles touch event. */
    private fun handleTouch(x: Float, y: Float) {
        touchState = TouchState(Point(x, y))
        listener?.let { it.onPositionChanged(getWheelPosition()) }
        invalidate()
    }

    /** Handles touch finish event. */
    private fun stopTouch() {
        touchState = null
        listener?.let { it.onFinished() }
        invalidate()
    }
}
