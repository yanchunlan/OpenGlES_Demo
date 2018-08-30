package com.example.gl.opengles_demo.day01

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * author: ycl
 * date: 2018-08-31 0:39
 * desc: 三角形
 */

class RendererActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val surfaceView = MyGlSurfaceView(this)
        surfaceView.setRenderer(MyRenderer())
        setContentView(surfaceView)
    }
}
