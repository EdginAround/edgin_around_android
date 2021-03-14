package com.edgin.around.rendering

import android.util.Log

import com.edgin.around.api.actors.Actor as ApiActor
import com.edgin.around.api.actors.ActorId
import com.edgin.around.api.geometry.*

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
    external fun zoomBy(zoom: Zoom)
    external fun rotateBy(angle: Angle)
    external fun tiltBy(angle: Angle)
    external fun highlight(x: Int, y: Int)
    external fun createRenderers(actors: Array<ActorBridge>)
    external fun deleteRenderers(ids: Array<ActorId>)

}
