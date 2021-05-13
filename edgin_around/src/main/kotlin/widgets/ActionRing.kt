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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

const val TAG: String = "EdginAround"

/** Widget displaying a joystick with action buttons (wheels). */
class ActionRing(context: Context, attrs: AttributeSet) : View(context, attrs) {
    /** Describes type of interaction with wheel. */
    enum class ReleaseVariant {
        CLICK, SLIDE, CANCEL
    }

    /** Point on the widget relatively to origin position of some action wheel. */
    private data class Point(val x: Float, val y: Float) {
        constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())
        constructor(x: Double, y: Double) : this(x.toFloat(), y.toFloat())

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
    public data class WheelPosition(
        /** Clock-wise angle measured from center-top axis. */
        val angle: Float,

        /** Number from `0.0` to `1.0` describing how far the wheel is from the center. */
        val magnitude: Float
    )

    /** Identifies a wheel. */
    private sealed class WheelId {
        class Main : WheelId()
        class Action(val index: Int) : WheelId()
    }

    /** Describes state of current touch gesture. */
    private data class TouchState(
        var point: Point,
        val wheel: WheelId,
        val maxClickRadius: Float,
        val cancelRadius: Float
    ) {
        val origin = point
        var isClick = true

        fun updatePoint(x: Float, y: Float) {
            point = Point(x, y)
            if (Point.distance(origin, point) > maxClickRadius) {
                isClick = false
            }
        }

        fun getReleaseVariant(): ReleaseVariant {
            return if (Point.distance(origin, point) > cancelRadius) {
                ReleaseVariant.CANCEL
            } else if (isClick) {
                ReleaseVariant.CLICK
            } else {
                ReleaseVariant.SLIDE
            }
        }
    }

    /** Listener for wheel events. */
    public interface WheelListener {
        fun onPositionChanged(position: WheelPosition)
        fun onFinished(variant: ReleaseVariant)
    }

    /** Aggregates information about an action button configurable by a `ActionRing` user. */
    public data class ActionConfig(
        val listener: WheelListener
    )

    /** Aggregates internal information about an action button. */
    private data class ActionButtonConfig(
        val action: ActionConfig,
        val center: Point
    )

    public val DEFAULT_RING_RADIUS: Int = 300
    public val DEFAULT_WHEEL_RADIUS: Int = 90
    public val DEFAULT_PADDING_SIZE: Int = 10
    public val DEFAULT_WHEEL_OFFSET: Int = 200
    public val DEFAULT_WHEEL_SEPARATION: Float = 0.4f * PI.toFloat()
    public val DEFAULT_AXIS_ANGLE: Float = 0.5f * PI.toFloat()

    public val DEFAULT_RING_STROKE_WIDTH: Int = 10
    public val DEFAULT_RING_STROKE_COLOR: Int = 0x55000000.toInt()
    public val DEFAULT_RING_FILL_COLOR: Int = 0x11000000.toInt()
    public val DEFAULT_HUB_STROKE_WIDTH: Int = 10
    public val DEFAULT_HUB_STROKE_COLOR: Int = 0x33000000.toInt()
    public val DEFAULT_HUB_FILL_COLOR: Int = 0x00000000.toInt()
    public val DEFAULT_WHEEL_STROKE_WIDTH: Int = 10
    public val DEFAULT_WHEEL_STROKE_COLOR: Int = 0xEE000000.toInt()
    public val DEFAULT_WHEEL_FILL_COLOR: Int = 0xBB000000.toInt()
    public val DEFAULT_BUTTON_STROKE_WIDTH: Int = 10
    public val DEFAULT_BUTTON_STROKE_COLOR: Int = 0xEE0000FF.toInt()
    public val DEFAULT_BUTTON_FILL_COLOR: Int = 0xBB0000FF.toInt()

    private var mainRingRadius: Int = DEFAULT_RING_RADIUS
    private var wheelRadius: Int = DEFAULT_WHEEL_RADIUS
    private var paddingSize: Int = DEFAULT_PADDING_SIZE
    private var wheelOffset: Int = DEFAULT_WHEEL_OFFSET
    private var wheelSeparation: Float = DEFAULT_WHEEL_SEPARATION
    private var axisAngle: Float = DEFAULT_AXIS_ANGLE

    private var ringStrokeWidth: Int = DEFAULT_RING_STROKE_WIDTH
    private var ringStrokeColor: Int = DEFAULT_RING_STROKE_COLOR
    private var ringFillColor: Int = DEFAULT_RING_FILL_COLOR
    private var hubStrokeWidth: Int = DEFAULT_HUB_STROKE_WIDTH
    private var hubStrokeColor: Int = DEFAULT_HUB_STROKE_COLOR
    private var hubFillColor: Int = DEFAULT_HUB_FILL_COLOR
    private var wheelStrokeWidth: Int = DEFAULT_WHEEL_STROKE_WIDTH
    private var wheelStrokeColor: Int = DEFAULT_WHEEL_STROKE_COLOR
    private var wheelFillColor: Int = DEFAULT_WHEEL_FILL_COLOR
    private var buttonStrokeWidth: Int = DEFAULT_BUTTON_STROKE_WIDTH
    private var buttonStrokeColor: Int = DEFAULT_BUTTON_STROKE_COLOR
    private var buttonFillColor: Int = DEFAULT_BUTTON_FILL_COLOR

    private var viewWidth: Int = calcMinSize()
    private var viewHeight: Int = calcMinSize()

    private var touchState: TouchState? = null
    private var listener: WheelListener? = null
    private var actionButtons: ArrayList<ActionButtonConfig> = arrayListOf()

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

    private val secondaryWheelFillPaint = Paint().apply {
        isAntiAlias = false
        style = Paint.Style.FILL
        color = buttonFillColor
    }

    private val secondaryWheelStrokePaint = Paint().apply {
        isAntiAlias = false
        style = Paint.Style.STROKE
        color = buttonStrokeColor
        strokeWidth = buttonStrokeWidth.toFloat()
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

        val wheel = touchState?.wheel
        drawRing(canvas)
        when (wheel) {
            is WheelId.Main -> {
                drawMainWheel(canvas)
            }
            is WheelId.Action -> {
                drawActionWheel(canvas, wheel.index)
            }
            null -> {
                drawMainWheel(canvas)
                drawActionWheels(canvas)
            }
        }
    }

    /** Overrides `View.onTouchEvent`. */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val state = touchState
        if (state == null) {
            when (event.getActionMasked()) {
                MotionEvent.ACTION_DOWN -> {
                    // Start new touch
                    startTouch(event.getX(), event.getY())
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
                    handleTouch(event.getX(), event.getY(), state)
                }
                MotionEvent.ACTION_UP -> {
                    handleTouch(event.getX(), event.getY(), state)
                    stopTouch(state)
                }
                MotionEvent.ACTION_CANCEL -> {
                    stopTouch(state)
                }
                else -> {
                    Log.i(TAG, "Unhandled event: ${event.getActionMasked()}")
                }
            }
        }

        return true
    }

    /** Sets radius of the ring. */
    public fun setRingRadius(radius: Int) {
        mainRingRadius = radius
    }

    /** Sets listener of events from the main wheel. */
    public fun setWheelListener(wheelListener: WheelListener) {
        listener = wheelListener
    }

    /** Returns current position of the main wheel. */
    public fun getWheelPosition(): WheelPosition {
        val state = touchState
        if (state != null) {
            val center = calcPrimaryPoint()
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

    /** Defines number of action buttons and their behaviour. */
    public fun configureActionButtons(configs: ArrayList<ActionConfig>) {
        val center = calcPrimaryPoint()
        actionButtons = ArrayList(
            configs.mapIndexed { i, it ->
                ActionButtonConfig(it, calcSecondaryPoint(center, i, configs.size))
            }
        )
        invalidate()
    }

    /**
     * Configures angle by which action button axis is rotated and how far the buttons are placed
     * from each other.
     */
    public fun configureAxis(newWheelSeparation: Float, newAxisAngle: Float) {
        wheelSeparation = newWheelSeparation
        axisAngle = newAxisAngle
    }

    /** Calculates the origin of the main wheel. */
    private fun calcPrimaryPoint(): Point {
        return Point(mainRingRadius + paddingSize, viewHeight - mainRingRadius - paddingSize)
    }

    /**
     * Calculates position of the actions wheel specified by `index` given position of the
     * primary wheel.
     */
    private fun calcSecondaryPoint(primary: Point, index: Int, size: Int): Point {
        val magnitude = index.toFloat() - 0.5 * size.toFloat()
        val angle = axisAngle - magnitude * wheelSeparation
        return Point(primary.x + wheelOffset * sin(angle), primary.y + wheelOffset * cos(angle))
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

    /** Returns ID placed in the given position or null if no wheel is placed there. */
    private fun findWheelIdForPoint(point: Point): WheelId? {
        val center = calcPrimaryPoint()
        if (Point.distance(center, point) < wheelRadius) {
            return WheelId.Main()
        }

        actionButtons.forEachIndexed { i, button ->
            if (Point.distance(button.center, point) < wheelRadius) {
                return WheelId.Action(i)
            }
        }

        return null
    }

    /** Returns listener for events for the wheel specified by `wheelId`. */
    private fun findListener(wheelId: WheelId): WheelListener? {
        return when (wheelId) {
            is WheelId.Main -> listener
            is WheelId.Action -> actionButtons.get(wheelId.index).action.listener
        }
    }

    /** Draws ring. */
    private fun drawRing(canvas: Canvas) {
        val ringPos = calcPrimaryPoint()
        canvas.apply {
            drawCircle(ringPos.x, ringPos.y, mainRingRadius.toFloat(), mainRingFillPaint)
            drawCircle(ringPos.x, ringPos.y, mainRingRadius.toFloat(), mainRingStrokePaint)
            drawCircle(ringPos.x, ringPos.y, wheelRadius.toFloat(), mainHubFillPaint)
            drawCircle(ringPos.x, ringPos.y, wheelRadius.toFloat(), mainHubStrokePaint)
        }
    }

    /** Draws the main wheel. */
    private fun drawMainWheel(canvas: Canvas) {
        val ringPos = calcPrimaryPoint()
        val wheelPos = calcWheelPoint(ringPos)
        canvas.apply {
            drawCircle(wheelPos.x, wheelPos.y, wheelRadius.toFloat(), mainWheelFillPaint)
            drawCircle(wheelPos.x, wheelPos.y, wheelRadius.toFloat(), mainWheelStrokePaint)
        }
    }

    /** Draws a single action button. */
    private fun drawActionButton(canvas: Canvas, config: ActionButtonConfig) {
        canvas.apply {
            drawCircle(config.center.x, config.center.y, wheelRadius.toFloat(), secondaryWheelFillPaint)
            drawCircle(config.center.x, config.center.y, wheelRadius.toFloat(), secondaryWheelStrokePaint)
        }
    }

    /** Draws a single action button. */
    private fun drawActionWheel(canvas: Canvas, wheelIndex: Int) {
        if (wheelIndex < actionButtons.size) {
            drawActionButton(canvas, actionButtons.get(wheelIndex))
        }
    }

    /** Draws all action buttons. */
    private fun drawActionWheels(canvas: Canvas) {
        actionButtons.forEach { drawActionButton(canvas, it) }
    }

    /** Handles start of touch event. */
    private fun startTouch(x: Float, y: Float) {
        val point = Point(x, y)
        val wheelId = findWheelIdForPoint(point)
        if (wheelId != null) {
            touchState = TouchState(point, wheelId, wheelRadius.toFloat(), mainRingRadius.toFloat())
            invalidate()
        }
    }

    /** Handles touch event. */
    private fun handleTouch(x: Float, y: Float, state: TouchState) {
        state.updatePoint(x, y)
        findListener(state.wheel)?.let { it.onPositionChanged(getWheelPosition()) }
        invalidate()
    }

    /** Handles touch finish event. */
    private fun stopTouch(state: TouchState) {
        findListener(state.wheel)?.let {
            it.onFinished(state.getReleaseVariant())
        }
        touchState = null
        invalidate()
    }
}
