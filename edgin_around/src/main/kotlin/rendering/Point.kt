package com.edgin.around.rendering

import com.edgin.around.api.geometry.Point as ApiPoint

class Point(internal val bridge: PointBridge) {
    constructor(theta: Float, phi: Float) : this(PointBridge()) {
        bridge.initialize(theta, phi)
    }

    constructor(point: ApiPoint) : this(point.theta, point.phi) {}

    fun getPhi(): Float = bridge.getPhi()
    fun getTheta(): Float = bridge.getTheta()
    fun getBridge(): PointBridge = bridge
}

class PointBridge() {
    constructor(theta: Float, phi: Float) : this() {
        initialize(theta, phi)
    }

    constructor(point: ApiPoint) : this(point.theta, point.phi) {}

    private var nativePtrHolder: Long = 0

    external fun initialize(theta: Float, phi: Float)
    external fun getTheta(): Float
    external fun getPhi(): Float
}
