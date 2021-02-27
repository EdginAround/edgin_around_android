package com.edgin.around

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class GameView : GLSurfaceView {
    constructor(context: Context): super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initialize()
    }

    fun initialize() {
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        setRenderer(GameRenderer());
    }
}

