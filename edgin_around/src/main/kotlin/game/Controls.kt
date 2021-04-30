package com.edgin.around.game

import com.edgin.around.rendering.WorldExpositor
import com.edgin.around.widgets.ActionRing

/** Implements behavior of GUI controls. */
class Controls(val proxy: Proxy, val world: WorldExpositor) {
    /** Implements behavior of the action ring responsible for player movements. */
    inner class MovementActionRingListener() : ActionRing.WheelListener {
        override fun onPositionChanged(position: ActionRing.WheelPosition) {
            walk(position.magnitude, position.angle)
        }

        override fun onFinished() {
            stop()
        }
    }

    /** Implements behavior of the action ring responsible for camera orientation. */
    inner class OrientationActionRingListener() : ActionRing.WheelListener {
        override fun onPositionChanged(position: ActionRing.WheelPosition) {
            walk(position.magnitude, position.angle)
        }

        override fun onFinished() {
            stop()
        }
    }

    /** Sends motion `move` if the displacement of the wheel is big enough. */
    private fun walk(speed: Float, bearing: Float) {
        if (speed > 0.5) {
            proxy.sendMotion(bearing)
        }
    }

    /** Sends stop ov the motion `move`. */
    private fun stop() {
        proxy.sendStop()
    }
}
