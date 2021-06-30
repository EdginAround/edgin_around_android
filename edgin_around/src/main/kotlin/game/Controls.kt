package com.edgin.around.game

import com.edgin.around.api.actors.ActorId
import com.edgin.around.api.enums.Hand
import com.edgin.around.rendering.WorldExpositor
import com.edgin.around.widgets.ActionRing

/** Implements behavior of GUI controls. */
class Controls(val proxy: Proxy, val world: WorldExpositor) {
    /** Implements behavior of the action ring responsible for player movements. */
    inner class MovementActionRingListener() : ActionRing.WheelListener {
        override fun onPositionChanged(position: ActionRing.WheelPosition) {
            walk(position.magnitude, position.angle)
        }

        override fun onFinished(variant: ActionRing.ReleaseVariant) {
            stop()
        }
    }

    /** Implements behavior of the action ring responsible for camera orientation. */
    inner class OrientationActionRingListener() : ActionRing.WheelListener {
        override fun onPositionChanged(position: ActionRing.WheelPosition) {
            walk(position.magnitude, position.angle)
        }

        override fun onFinished(variant: ActionRing.ReleaseVariant) {
            stop()
        }
    }

    /** Implements behavior of chosen action ring buttons. */
    inner class UseHandListener(val hand: Hand) : ActionRing.WheelListener {
        override fun onPositionChanged(position: ActionRing.WheelPosition) {
            // Nothing to do
        }

        override fun onFinished(variant: ActionRing.ReleaseVariant) {
            if (variant == ActionRing.ReleaseVariant.CLICK) {
                activateHand(hand, world.getHighlightedActorId())
            }
        }
    }

    /** Implements behavior of chosen action ring buttons. */
    /* TODO: Implement actual throwing. */
    inner class ThrowItemListener(val hand: Hand) : ActionRing.WheelListener {
        override fun onPositionChanged(position: ActionRing.WheelPosition) {
            // Nothing to do
        }

        override fun onFinished(variant: ActionRing.ReleaseVariant) {
            if (variant == ActionRing.ReleaseVariant.CLICK) {
                activateHand(hand, world.getHighlightedActorId())
            }
        }
    }

    /**
     * Retuns a list of actions possible to perform with item specified by `codename`.
     * Null `codename` refers to empty, bare hand.
     */
    public fun getItemActions(codename: String?, hand: Hand): ArrayList<ActionRing.ActionConfig> {
        return when (codename) {
            "axe" -> arrayListOf(
                ActionRing.ActionConfig(UseHandListener(hand)),
                ActionRing.ActionConfig(ThrowItemListener(hand))
            )
            null -> arrayListOf(
                ActionRing.ActionConfig(UseHandListener(hand))
            )
            else -> arrayListOf(
                ActionRing.ActionConfig(ThrowItemListener(hand))
            )
        }
    }

    /** Sends hand activation `move`. */
    private fun activateHand(hand: Hand, objectId: ActorId) {
        proxy.sendHandActivation(hand, objectId)
    }

    /** Sends stop of the motion `move`. */
    private fun stop() {
        proxy.sendMotionStop()
    }

    /** Sends motion `move` if the displacement of the wheel is big enough. */
    private fun walk(speed: Float, bearing: Float) {
        if (speed > 0.5) {
            proxy.sendMotionStart(bearing)
        }
    }
}
