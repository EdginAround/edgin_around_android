package com.edgin.around.game

import android.os.SystemClock
import android.util.Log
import com.edgin.around.api.actions.Action
import com.edgin.around.api.actions.ActorCreationAction
import com.edgin.around.api.actions.ActorDeletionAction
import com.edgin.around.api.actions.ConfigurationAction
import com.edgin.around.api.actions.CraftBeginAction
import com.edgin.around.api.actions.CraftEndAction
import com.edgin.around.api.actions.DamageAction
import com.edgin.around.api.actions.IdleAction
import com.edgin.around.api.actions.InventoryUpdateAction
import com.edgin.around.api.actions.LocalizationAction
import com.edgin.around.api.actions.MotionAction
import com.edgin.around.api.actions.PickBeginAction
import com.edgin.around.api.actions.PickEndAction
import com.edgin.around.api.actions.StatUpdateAction
import com.edgin.around.api.actors.ActorId
import com.edgin.around.api.enums.Attachment
import com.edgin.around.api.enums.Hand

internal const val MAX_PICK_DISTANCE: Float = 1.0f

enum class AnimationName(val value: String) {
    IDLE("idle"),
    WALK("walk"),
    PICK("pick"),
    DAMAGED("damaged"),
    SWING_LEFT("swing_left"),
    SWING_RIGHT("swing_right")
}

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

    protected fun refreshHighlight(context: MotiveContext) {
        val heroId = context.scene.getHeroId()
        val heroPosition = context.scene.getActorPosition(heroId)
        if (heroPosition == null) {
            return
        }

        val actors = context.scene.findClosestActors(heroPosition, MAX_PICK_DISTANCE)

        // The hero will always be the closest actor
        if (actors.size > 1) {
            context.world.setHighlightedActorId(actors[1])
        } else {
            context.world.removeHighlight()
        }
    }
}

data class ActorCreationMotive(val action: ActorCreationAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.scene.createActors(action.actors)
        context.world.createRenderers(action.actors)
        refreshHighlight(context)
        expire()
    }
}

data class ActorDeletionMotive(val action: ActorDeletionAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.scene.deleteActors(action.actorIds)
        context.world.deleteRenderers(action.actorIds)
        refreshHighlight(context)
        expire()
    }
}

data class ConfigurationMotive(val action: ConfigurationAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.scene.configure(action.heroActorId, action.elevation)
        expire()
    }
}

data class CraftBeginMotive(val action: CraftBeginAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        expire()
    }
}

data class CraftEndMotive(val action: CraftEndAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        expire()
    }
}

data class DamageMotive(val action: DamageAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        if (action.hand == Hand.LEFT) {
            context.world.playAnimation(action.dealerId, AnimationName.SWING_LEFT.value)
        } else {
            context.world.playAnimation(action.dealerId, AnimationName.SWING_LEFT.value)
        }
        context.world.playAnimation(action.receiverId, AnimationName.DAMAGED.value)

        // TODO: Play damage sound
        expire()
    }
}

class DummyMotive() : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        expire()
    }
}

data class IdleMotive(val action: IdleAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.world.playAnimation(action.actorId, AnimationName.IDLE.value)
        expire()
    }
}

data class InventoryUpdateMotive(val action: InventoryUpdateAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.gui.setInventory(action.inventory)
        context.scene.hideActors(action.inventory.getAllIds().toLongArray())

        val leftItem = action.inventory.getHand(Hand.LEFT)
        if (leftItem != null) {
            context.world.attachActor(Attachment.LEFT_ITEM, action.ownerId, leftItem.id)
        } else {
            context.world.detachActor(Attachment.LEFT_ITEM, action.ownerId)
        }

        val rightItem = action.inventory.getHand(Hand.RIGHT)
        if (rightItem != null) {
            context.world.attachActor(Attachment.RIGHT_ITEM, action.ownerId, rightItem.id)
        } else {
            context.world.detachActor(Attachment.RIGHT_ITEM, action.ownerId)
        }

        expire()
    }
}

data class LocalizationMotive(val action: LocalizationAction) : Motive() {
    override fun getActorId(): ActorId {
        return action.actorId
    }

    override fun tick(interval: Long, context: MotiveContext) {
        context.scene.setActorPosition(action.actorId, action.position.theta, action.position.phi)
        refreshHighlight(context)
        expire()
    }
}

data class MotionMotive(val action: MotionAction) : Motive() {
    override fun getActorId(): ActorId? {
        return action.actorId
    }

    override fun expired(): Boolean {
        return isTimedOut(action.getDurationMillis())
    }

    override fun doTick(interval: Long, context: MotiveContext) {
        val distance = 0.001f * action.speed * interval
        context.scene.moveActorBy(action.actorId, distance, action.bearing)
        if (getTickCount() == 0) {
            context.world.playAnimation(action.actorId, AnimationName.WALK.value)
        }
        refreshHighlight(context)
    }
}

data class PickBeginMotive(val action: PickBeginAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.world.playAnimation(action.who, AnimationName.PICK.value)
        expire()
    }
}

data class PickEndMotive(val action: PickEndAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.world.playAnimation(action.who, AnimationName.IDLE.value)
        expire()
    }
}

data class StatUpdateMotive(val action: StatUpdateAction) : Motive() {
    override fun tick(interval: Long, context: MotiveContext) {
        context.gui.setStats(action.stats)
        expire()
    }
}

class MotiveFactory {
    fun build(action: Action): Motive {
        return when (action) {
            is ActorCreationAction -> ActorCreationMotive(action)
            is ActorDeletionAction -> ActorDeletionMotive(action)
            is ConfigurationAction -> ConfigurationMotive(action)
            is CraftBeginAction -> CraftBeginMotive(action)
            is CraftEndAction -> CraftEndMotive(action)
            is DamageAction -> DamageMotive(action)
            is IdleAction -> IdleMotive(action)
            is LocalizationAction -> LocalizationMotive(action)
            is MotionAction -> MotionMotive(action)
            is PickBeginAction -> PickBeginMotive(action)
            is PickEndAction -> PickEndMotive(action)
            is StatUpdateAction -> StatUpdateMotive(action)
            is InventoryUpdateAction -> InventoryUpdateMotive(action)
            else -> {
                Log.e(TAG, "Failed to cast animation into motive: $action")
                DummyMotive()
            }
        }
    }
}
