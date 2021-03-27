package com.edgin.around.api.geometry

import com.google.gson.annotations.SerializedName

typealias Angle = Float
typealias Coordinate = Float
typealias Dimention = Int
typealias Distance = Float
typealias Zoom = Int

data class Point(
    @SerializedName("theta")
    val theta: Coordinate,

    @SerializedName("phi")
    val phi: Coordinate
)

data class Terrain(
    @SerializedName("type")
    val variant: String,

    @SerializedName("origin")
    val origin: Point
)

data class Elevation(
    @SerializedName("radius")
    val radius: Distance,

    @SerializedName("terrain")
    val terrain: ArrayList<Terrain>
)
