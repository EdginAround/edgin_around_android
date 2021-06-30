package com.edgin.around.rendering

import com.edgin.around.api.actors.ActorId
import com.edgin.around.api.actors.ActorIdArray
import com.edgin.around.api.enums.Attachment
import com.edgin.around.api.geometry.Angle
import com.edgin.around.api.geometry.Zoom
import com.edgin.around.api.actors.Actor as ApiActor

class WorldExpositor(
    resourceDir: String,
    width: Int,
    height: Int
) {
    internal var bridge = WorldExpositorBridge(resourceDir, width, height)

    fun resize(width: Int, height: Int) {
        bridge.resize(width, height)
    }

    fun render(scene: Scene) {
        bridge.render(scene.bridge)
    }

    fun getBearing(): Angle {
        return bridge.getBearing()
    }

    fun getHighlightedActorId(): ActorId {
        return bridge.getHighlightedActorId()
    }

    fun setHighlightedActorId(actorId: ActorId) {
        bridge.setHighlightedActorId(actorId)
    }

    fun removeHighlight() {
        bridge.removeHighlight()
    }

    fun zoomBy(zoom: Zoom) {
        bridge.zoomBy(zoom)
    }

    fun rotateBy(angle: Angle) {
        bridge.rotateBy(angle)
    }

    fun tiltBy(angle: Angle) {
        bridge.tiltBy(angle)
    }

    fun createRenderers(apiActors: Array<ApiActor>) {
        val actors: Array<ActorBridge> = apiActors.map { ActorBridge(it) }.toTypedArray()
        bridge.createRenderers(actors)
    }

    fun deleteRenderers(actorIds: ActorIdArray) {
        bridge.deleteRenderers(actorIds)
    }

    fun playAnimation(actorId: ActorId, animationName: String) {
        bridge.playAnimation(actorId, animationName)
    }

    fun attachActor(attachment: Attachment, baseActorId: ActorId, attachedActorId: ActorId) {
        bridge.attachActor(attachment.value, baseActorId, attachedActorId)
    }

    fun detachActor(attachment: Attachment, baseActorId: ActorId) {
        bridge.detachActor(attachment.value, baseActorId)
    }
}

class WorldExpositorBridge(
    resourceDir: String,
    width: Int,
    height: Int
) {
    private var nativePtrHolder: Long = 0

    init {
        initialize(resourceDir, width, height)
    }

    external fun initialize(
        resourceDir: String,
        width: Int,
        height: Int
    )
    external fun resize(width: Int, height: Int)
    external fun render(scene: SceneBridge)
    external fun getBearing(): Angle
    external fun getHighlightedActorId(): ActorId
    external fun setHighlightedActorId(actorId: ActorId)
    external fun removeHighlight()
    external fun zoomBy(zoom: Zoom)
    external fun rotateBy(angle: Angle)
    external fun tiltBy(angle: Angle)
    external fun createRenderers(actors: Array<ActorBridge>)
    external fun deleteRenderers(ids: ActorIdArray)
    external fun playAnimation(actorId: ActorId, animationName: String)
    external fun attachActor(hookName: String, baseActorId: ActorId, attachedActorId: ActorId)
    external fun detachActor(hookName: String, baseActorId: ActorId)
}
