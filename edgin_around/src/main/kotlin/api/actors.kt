package com.edgin.around.api.actors

import com.edgin.around.api.geometry.Point
import com.google.gson.annotations.SerializedName

typealias ActorId = Long
typealias ActorIdArray = LongArray

data class Actor(
    @SerializedName("id")
    val id: ActorId,

    @SerializedName("entity_name")
    val entityName: String,

    @SerializedName("position")
    val position: Point?
)
