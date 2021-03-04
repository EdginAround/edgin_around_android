package com.edgin.around

class PreviewExpositor(
        sprite_dir: String,
        skin_name: String,
        saml_name: String,
        animation_name: String,
        width: Int,
        height: Int
) {
    var bridge = PreviewExpositorBridge()

    init {
        bridge.initialize(sprite_dir, skin_name, saml_name, animation_name, width, height)
    }

    fun resize(width: Int, height: Int) {
        bridge.resize(width, height)
    }

    fun render() {
        bridge.render()
    }
}

class PreviewExpositorBridge {
    private var nativePtrHolder: Long = 0

    external fun initialize(
        sprite_dir: String,
        skin_name: String,
        saml_name: String,
        animation_name: String,
        width: Int,
        height: Int
    )
    external fun resize(width: Int, height: Int)
    external fun render()
}
