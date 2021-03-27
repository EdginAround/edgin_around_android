package com.edgin.around.app

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class GameView : GLSurfaceView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun initialize(renderer: GLSurfaceView.Renderer) {
        setEGLContextClientVersion(2)
        setPreserveEGLContextOnPause(true)
        setRenderer(renderer)
    }
}
