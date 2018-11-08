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
class SGLRender : GLSurfaceView.Renderer, AnkoLogger {
    public var aFilter: AFilter? = null

    private var bitmap: Bitmap? = null
    private var width: Int = 0
    private var height: Int = 0
    private var config: EGLConfig? = null
    private var refreshFlag = false

    constructor(c: Context) {
        aFilter = ContrastColorFilter(c, Filter.NONE)
    }

    // 调用一次之后就会调用所有具体类的所有方法
    fun refresh() {
        refreshFlag = true
    }

    override fun onDrawFrame(gl: GL10?) {
        info { "onDrawFrame" }
        if (refreshFlag && width != 0 && height != 0) {
            refreshFlag = false
            aFilter?.onSurfaceCreated(gl, config)
            aFilter?.onSurfaceChanged(gl, width, height)
        }
        aFilter?.onDrawFrame(gl)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        info { "onSurfaceChanged" }
        this.width = width
        this.height = height
        aFilter?.onSurfaceChanged(gl, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        info { "onSurfaceCreated" }
        this.config = config
        aFilter?.onSurfaceCreated(gl, config)

    }

    fun setFilter(filter: AFilter) {
        aFilter = filter

        // 对新的 AFilter 设置bitmap，并开启开关，使全部调用方法
        refresh()
        if (bitmap != null) {
            aFilter?.setBitmap(bitmap)
        }
    }

    fun setImage(b: Bitmap?) {
        this.bitmap = b
        aFilter?.setBitmap(b)
    }


}