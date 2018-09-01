package com.example.gl.opengles_demo.day02

import android.opengl.GLSurfaceView
import android.os.Bundle
import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer
import com.example.gl.opengles_demo.day01.MyGlSurfaceView

/**
 * author: ycl
 * date: 2018-09-01 11:14
 * desc: 模板
 */
class StencilActivity : AbstractMyActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surfaceView = MyGlSurfaceView(this)
        render = getAbstractMyRenderer()

        // 设置深度，模板得值
//        surfaceView.setEGLConfigChooser(5, 6, 5, 0, 8, 8) // 硬件可能不支持就会报错，模拟器
        surfaceView.setRenderer(render)

        //GLSurfaceView.RENDERMODE_CONTINUOUSLY:持续渲染(默认)
        //GLSurfaceView.RENDERMODE_WHEN_DIRTY:脏渲染,命令渲染
        surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        setContentView(surfaceView)

    }

    override fun abstractInit(): Boolean = false

    override fun getAbstractMyRenderer(): AbstractMyRenderer = MyStencilRenderer()
}