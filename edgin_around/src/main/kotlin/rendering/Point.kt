package com.edgin.around.rendering

import com.edgin.around.api.geometry.Point as ApiPoint

class Point(val theta: Float, val phi: Float) {
    internal val bridge = PointBridge(theta, phi)

    constructor(point: ApiPoint): this(point.theta, point.phi) {}
}

class PointBridge(val theta: Float, val phi: Float) {
    private var nativePtrHolder: Long = 0

    init {
        initialize(theta, phi)
    }

    constructor(point: ApiPoint): this(point.theta, point.phi) {}

    external fun initialize(theta: Float, phi: Float)
}

