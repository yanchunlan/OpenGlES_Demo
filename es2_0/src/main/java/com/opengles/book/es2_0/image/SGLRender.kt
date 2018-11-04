package com.opengles.book.es2_0.image

import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import com.opengles.book.es2_0.image.filter.AFilter
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.properties.Delegates

/**
 * author: ycl
 * date: 2018-11-04 23:11
 * desc:
 */
class SGLRender : GLSurfaceView.Renderer {
    private val aFilter: AFilter by Delegates.notNull<AFilter>()

    constructor(view: GLSurfaceView){

    }


    override fun onDrawFrame(gl: GL10?) {

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

    fun setFilter(filter: AFilter) {


    }

    fun setImage(decodeStream: Bitmap?) {


    }


}