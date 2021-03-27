package com.edgin.around.rendering

import com.edgin.around.api.actors.ActorId
import com.edgin.around.api.actors.Actor as ApiActor

class Actor(id: ActorId, entityName: String, position: Point?) {
    internal var bridge = ActorBridge(id, entityName, position?.bridge)

    constructor(actor: ApiActor) :
        this(actor.id, actor.entityName, actor.position?.let { Point(it) }) {}
}

class ActorBridge(id: ActorId, entityName: String, position: PointBridge?) {
    private var nativePtrHolder: Long = 0

    init {
        initialize(id, entityName, position)
    }

    constructor(actor: ApiActor) :
        this(actor.id, actor.entityName, actor.position?.let { PointBridge(it) }) {}

    external fun initialize(id: ActorId, entityName: String, position: PointBridge?)
}
