package com.edgin.around.rendering

import com.edgin.around.api.actors.ActorId
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

    fun createRenderers(apiActors: Array<ApiActor>) {
        val actors: Array<ActorBridge> = apiActors.map { ActorBridge(it) }.toTypedArray()
        bridge.createRenderers(actors)
    }

    fun deleteRenderers(actorIds: Array<ActorId>) {
        bridge.deleteRenderers(actorIds)
    }

    fun playAnimation(actorId: ActorId, animationName: String) {
        bridge.playAnimation(actorId, animationName)
    }

    fun attachActor(hookName: String, baseActorId: ActorId, attachedActorId: ActorId) {
        bridge.attachActor(hookName, baseActorId, attachedActorId)
    }

    fun detachActor(hookName: String, baseActorId: ActorId) {
        bridge.detachActor(hookName, baseActorId)
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
    external fun deleteRenderers(ids: Array<ActorId>)
    external fun playAnimation(actorId: ActorId, animationName: String)
    external fun attachActor(hookName: String, baseActorId: ActorId, attachedActorId: ActorId)
    external fun detachActor(hookName: String, baseActorId: ActorId)
}
