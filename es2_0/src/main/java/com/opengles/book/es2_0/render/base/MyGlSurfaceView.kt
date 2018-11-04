package com.opengles.book.es2_0.render.base

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 * author: ycl
 * date: 2018-10-28 14:44
 * desc:
 */
class MyGlSurfaceView : GLSurfaceView {
    private var mMyRenderer: MyRenderer? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        setEGLContextClientVersion(2)// 使用gl2.0
        mMyRenderer = MyRenderer(this@MyGlSurfaceView)
        setRenderer(mMyRenderer)
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY // 渲染一次
    }

    fun setRendererClass(rendererClass: Class<out BaseRenderer>) {
        try {
            mMyRenderer?.setRendererClass(rendererClass)
            // 因为回到这个页面，需要重建页面，所以会执行三个方法，就不需要requestRenderer
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        requestRender()
    }

}