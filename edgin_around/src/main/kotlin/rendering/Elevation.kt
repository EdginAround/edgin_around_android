package com.edgin.around.rendering

import com.edgin.around.api.geometry.Coordinate
import com.edgin.around.api.geometry.Elevation as ApiElevation

class Elevation(radius: Float) {
    internal var bridge = ElevationBridge(radius)

    constructor(elevation: ApiElevation) : this(elevation.radius) {
        for (terrain in elevation.terrain) {
            addTerrain(terrain.variant, terrain.origin.theta, terrain.origin.phi)
        }
    }

    fun addTerrain(name: String, theta: Coordinate, phi: Coordinate) {
        bridge.addTerrain(name, theta, phi)
    }
}

class ElevationBridge(radius: Float) {
    private var nativePtrHolder: Long = 0

    init {
        initialize(radius)
    }

    constructor(elevation: ApiElevation) : this(elevation.radius) {
        for (terrain in elevation.terrain) {
            addTerrain(terrain.variant, terrain.origin.theta, terrain.origin.phi)
        }
    }

    external fun initialize(radius: Float)
    external fun addTerrain(name: String, theta: Coordinate, phi: Coordinate)
}
