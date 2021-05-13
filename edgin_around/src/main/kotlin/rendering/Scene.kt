package com.edgin.around.rendering

import com.edgin.around.api.actors.ActorId
import com.edgin.around.api.actors.ActorIdArray
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

    fun getHeroId(): ActorId {
        return bridge.getHeroId()
    }

    fun deleteActors(actorIds: ActorIdArray) {
        bridge.deleteActors(actorIds)
    }

    fun hideActors(actorIdsArray: ActorIdArray) {
        bridge.hideActors(actorIdsArray)
    }

    fun getRadius(): Float {
        return bridge.getRadius()
    }

    fun findClosestActors(point: Point, maxDistance: Float): ActorIdArray {
        return bridge.findClosestActors(point.getTheta(), point.getPhi(), maxDistance)
    }

    fun getActorPosition(actorId: ActorId): Point? {
        val pointBridge = bridge.getActorPosition(actorId)
        return if (pointBridge != null) Point(pointBridge) else null
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
    external fun getHeroId(): ActorId
    external fun createActors(actors: Array<ActorBridge>)
    external fun deleteActors(actorIdsArray: ActorIdArray)
    external fun hideActors(actorIdsArray: ActorIdArray)
    external fun getRadius(): Float
    external fun findClosestActors(theta: Float, phi: Float, maxDistance: Float): ActorIdArray
    external fun getActorPosition(actorId: ActorId): PointBridge?
    external fun setActorPosition(actorId: ActorId, theta: Float, phi: Float)
    external fun moveActorBy(actorId: ActorId, distance: Float, bearing: Float)
}
