package com.edgin.around.rendering

class PreviewExpositor(
        spriteDir: String,
        skinName: String,
        samlName: String,
        animationName: String,
        width: Int,
        height: Int
) {
    var bridge = PreviewExpositorBridge(spriteDir, skinName, samlName, animationName, width, height)

    fun resize(width: Int, height: Int) {
        bridge.resize(width, height)
    }

    fun render() {
        bridge.render()
    }
}

class PreviewExpositorBridge(
        spriteDir: String,
        skinName: String,
        samlName: String,
        animationName: String,
        width: Int,
        height: Int
) {
    private var nativePtrHolder: Long = 0

    init {
        initialize(spriteDir, skinName, samlName, animationName, width, height)
    }

    external fun initialize(
        spriteDir: String,
        skinName: String,
        samlName: String,
        animationName: String,
        width: Int,
        height: Int
    )
    external fun resize(width: Int, height: Int)
    external fun render()
}
