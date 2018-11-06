package com.opengles.book.es2_0.vary

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class VaryingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(GLSurfaceView(this)
                .apply {
                    setEGLContextClientVersion(2)
                    setRenderer(VaryRenderer())
                    renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
                })
    }
}
