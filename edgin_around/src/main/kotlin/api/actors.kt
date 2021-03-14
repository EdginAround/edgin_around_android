package com.edgin.around.api.actors

import com.google.gson.annotations.SerializedName

import com.edgin.around.api.geometry.Point

typealias ActorId = Long

data class Actor (
    @SerializedName("id")
    val id: ActorId,

    @SerializedName("entity_name")
    val entityName: String,

    @SerializedName("position")
    val position: Point?
)

