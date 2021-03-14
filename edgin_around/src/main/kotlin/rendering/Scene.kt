package com.edgin.around.rendering

import com.edgin.around.api.actors.ActorId
import com.edgin.around.api.actors.Actor as ApiActor
import com.edgin.around.api.geometry.Elevation as ApiElevation

class Scene {
    internal var bridge = SceneBridge()

    fun configure(heroActorId: ActorId, elevation: ApiElevation) {
        bridge.configure(heroActorId, ElevationBridge(elevation))
    }

    fun createActors(apiActors: Array<ApiActor>) {
        val actors: Array<ActorBridge> = apiActors.map { ActorBridge(it) }.toTypedArray()
        bridge.createActors(actors)
    }

    fun deleteActors(actorIds: Array<ActorId>) {
        bridge.deleteActors(actorIds)
    }

    fun hideActors(actorIdsArray: Array<ActorId>) {
        bridge.hideActors(actorIdsArray)
    }

    fun getRadius(): Float {
        return bridge.getRadius()
    }

    fun setActorPosition(actorId: ActorId, theta: Float, phi: Float) {
        bridge.setActorPosition(actorId, theta, phi)
    }

    fun moveActorBy(actorId: ActorId, distance: Float, bearing: Float) {
        bridge.moveActorBy(actorId, distance, bearing)
    }
}

class SceneBridge {
    private var nativePtrHolder: Long = 0

    init {
        initialize()
    }

    external fun initialize()
    external fun configure(heroActorId: ActorId, elevation: ElevationBridge)
    external fun createActors(actors: Array<ActorBridge>)
    external fun deleteActors(actorIdsArray: Array<ActorId>)
    external fun hideActors(actorIdsArray: Array<ActorId>)
    external fun getRadius(): Float
    external fun setActorPosition(actorId: ActorId, theta: Float, phi: Float)
    external fun moveActorBy(actorId: ActorId, distance: Float, bearing: Float)
}
