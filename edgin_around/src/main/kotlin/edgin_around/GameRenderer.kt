package com.edgin.around

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig

class GameRenderer: GLSurfaceView.Renderer {
    private val DEFAULT_WIDTH = 800
    private val DEFAULT_HEIGHT = 800

    private var expositor: PreviewExpositor? = null

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        expositor = PreviewExpositor(
            "/sdcard/edgin_around/resources/sprites/",
            "pirate",
            "pirate.saml",
            "idle",
            DEFAULT_WIDTH,
            DEFAULT_HEIGHT
        )
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        expositor?.let { it.resize(width, height) }
    }

    override fun onDrawFrame(gl: GL10) {
        expositor?.let { it.render() }
    }
}

