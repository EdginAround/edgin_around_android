package com.edgin.around.app

import android.opengl.GLSurfaceView
import com.edgin.around.game.Thruster
import com.edgin.around.rendering.Scene
import com.edgin.around.rendering.WorldExpositor
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(
    var expositor: WorldExpositor,
    val scene: Scene,
    val thruster: Thruster
) : GLSurfaceView.Renderer {
    private val DEFAULT_WIDTH = 800
    private val DEFAULT_HEIGHT = 800

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // Nothing to do
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        expositor.resize(width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        thruster.thrust()
        expositor.render(scene)
    }
}
