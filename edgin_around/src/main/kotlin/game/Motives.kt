package com.edgin.around.game

import android.os.SystemClock
import android.util.Log
import com.edgin.around.api.actions.Action
import com.edgin.around.api.actions.ConfigurationAction
import com.edgin.around.api.actions.CraftEndAction
import com.edgin.around.api.actions.CraftStartAction
import com.edgin.around.api.actions.CreateActorsAction
import com.edgin.around.api.actions.DamageAction
import com.edgin.around.api.actions.DeleteActorsAction
import com.edgin.around.api.actions.LocalizeAction
import com.edgin.around.api.actions.MovementAction
import com.edgin.around.api.actions.PickEndAction
import com.edgin.around.api.actions.PickStartAction
import com.edgin.around.api.actions.StatUpdateAction
import com.edgin.around.api.actions.UpdateInventoryAction
import com.edgin.around.api.actors.ActorId

open class Motive {
    private val startTime = SystemClock.uptimeMillis()
    private var isExpired = false
    private var tickCount = 0

    open fun expired(): Boolean {
        return isExpired
    }

    open fun getActorId(): ActorId? {
        return null
    }

    open fun tick(interval: Long, context: MotiveContext) {
        doTick(interval, context)
        tickCount += 1
    }

    protected open fun expire() {
        isExpired = true
    }

    protected fun isTimedOut(timeout: Long): Boolean {
        return startTime + timeout < SystemClock.uptimeMillis()
    }

    protected fun getTickCount(): Int {
        return tickCount
    }

    protected open fun doTick(interval: Long, context: MotiveContext) {
        // Do nothing by default
    }
}

class DummyMotive() : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        expire()
    }
}

data class ConfigurationMotive(val action: ConfigurationAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.scene.configure(action.heroActorId, action.elevation)
        expire()
    }
}

data class CraftStartMotive(val action: CraftStartAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        expire()
    }
}

data class CraftEndMotive(val action: CraftEndAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        expire()
    }
}

data class CreateActorsMotive(val action: CreateActorsAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.scene.createActors(action.actors)
        context.world.createRenderers(action.actors)
        expire()
    }
}

data class DeleteActorsMotive(val action: DeleteActorsAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.scene.deleteActors(action.actorIds)
        context.world.deleteRenderers(action.actorIds)
        expire()
    }
}

data class MovementMotive(val action: MovementAction) : Motive() {
    override fun getActorId(): ActorId? {
        return action.actorId
    }

    override fun expired(): Boolean {
        return isTimedOut(action.getDurationMillis())
    }

    override fun doTick(interval: Long, context: MotiveContext) {
        val distance = 0.001f * action.speed * interval
        context.scene.moveActorBy(action.actorId, distance, action.bearing)
        // TODO: Play walk animation
    }
}

data class LocalizeMotive(val action: LocalizeAction) : Motive() {
    override fun getActorId(): ActorId {
        return action.actorId
    }

    override fun tick(interval: Long, context: MotiveContext) {
        context.scene.setActorPosition(action.actorId, action.position.theta, action.position.phi)
        // TODO: Play idle animation
        expire()
    }
}

data class StatUpdateMotive(val action: StatUpdateAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.gui.setStats(action.stats)
        expire()
    }
}

data class PickStartMotive(val action: PickStartAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        // TODO: Play pick animation
        expire()
    }
}

data class PickEndMotive(val action: PickEndAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        // TODO: Play idle animation
        expire()
    }
}

data class UpdateInventoryMotive(val action: UpdateInventoryAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.gui.setInventory(action.inventory)
        context.scene.hideActors(action.inventory.getAllIds().toTypedArray())
        // TODO: Update skeleton with hand content
        expire()
    }
}

data class DamageMotive(val action: DamageAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        // TODO: Play animation for dealer
        // TODO: Play animation for receiver
        // TODO: Play damage sound
        expire()
    }
}

class MotiveFactory {
    fun build(action: Action): Motive {
        return when (action) {
            is ConfigurationAction -> ConfigurationMotive(action)
            is CreateActorsAction -> CreateActorsMotive(action)
            is DeleteActorsAction -> DeleteActorsMotive(action)
            is MovementAction -> MovementMotive(action)
            is LocalizeAction -> LocalizeMotive(action)
            is StatUpdateAction -> StatUpdateMotive(action)
            is PickStartAction -> PickStartMotive(action)
            is PickEndAction -> PickEndMotive(action)
            is UpdateInventoryAction -> UpdateInventoryMotive(action)
            is DamageAction -> DamageMotive(action)
            is CraftStartAction -> CraftStartMotive(action)
            is CraftEndAction -> CraftEndMotive(action)
            else -> {
                Log.e(TAG, "Failed to cast animation into motive: $action")
                DummyMotive()
            }
        }
    }
}
