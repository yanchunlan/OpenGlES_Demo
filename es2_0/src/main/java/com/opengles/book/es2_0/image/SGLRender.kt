package com.opengles.book.es2_0.image

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import com.opengles.book.es2_0.image.filter.AFilter
import com.opengles.book.es2_0.image.filter.ContrastColorFilter
import com.opengles.book.es2_0.image.filter.Filter
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * author: ycl
 * date: 2018-11-04 23:11
 * desc: 包装类 - > 真正具体的实现在AFilter里面
 */
class SGLRender : GLSurfaceView.Renderer,AnkoLogger {
    private var aFilter: AFilter? = null

    constructor(c: Context) {
        aFilter = ContrastColorFilter(c, Filter.NONE)
    }


    override fun onDrawFrame(gl: GL10?) {
        info { "onDrawFrame" }
        aFilter?.onDrawFrame(gl)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        info { "onSurfaceChanged" }
        aFilter?.onSurfaceChanged(gl, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        info { "onSurfaceCreated" }
        aFilter?.onSurfaceCreated(gl, config)

    }

    fun setFilter(filter: AFilter) {
        aFilter = filter
    }

    fun setImage(b: Bitmap?) {
        aFilter?.setBitmap(b)
    }


}