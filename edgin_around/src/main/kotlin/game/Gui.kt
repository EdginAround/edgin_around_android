package com.edgin.around.game

import android.app.Activity
import com.edgin.around.api.enums.Hand
import com.edgin.around.api.inventory.Inventory
import com.edgin.around.api.stats.Stats
import com.edgin.around.widgets.ActionRing

const val TAG: String = "EdginAround"

// TODO: Finish GUI callbacks.
class Gui(
    val activity: Activity,
    val controls: Controls,
    val actionRingLeft: ActionRing,
    val actionRingRight: ActionRing
) {
    init {
        actionRingLeft.setWheelListener(controls.MovementActionRingListener())
        actionRingRight.setWheelListener(controls.OrientationActionRingListener())
    }

    @Suppress("UNUSED_PARAMETER")
    fun setStats(stats: Stats) {}

    fun setInventory(inventory: Inventory) {
        activity.runOnUiThread {
            val leftConfigs = controls.getItemActions(inventory.leftHand?.codename, Hand.LEFT)
            val rightConfigs = controls.getItemActions(inventory.rightHand?.codename, Hand.RIGHT)

            actionRingLeft.configureActionButtons(leftConfigs)
            actionRingRight.configureActionButtons(rightConfigs)
        }
    }
}
