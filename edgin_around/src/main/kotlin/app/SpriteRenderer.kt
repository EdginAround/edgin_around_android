package com.edgin.around.app

import android.opengl.GLSurfaceView
import com.edgin.around.rendering.PreviewExpositor
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SpriteRenderer(val spriteDir: String) : GLSurfaceView.Renderer {
    private var expositor: PreviewExpositor? = null

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        expositor = PreviewExpositor(
            spriteDir,
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
