package com.edgin.around

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig

class GameRenderer : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.9f, 0.9f,0.1f,1f);
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}

