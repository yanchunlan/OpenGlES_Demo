package com.opengles.book.es2_0.image

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.opengles.book.es2_0.image.filter.AFilter
import kotlin.properties.Delegates

/**
 * author: ycl
 * date: 2018-11-04 23:09
 * desc:
 */
class SGLView : GLSurfaceView {

    private var sglRender: SGLRender by Delegates.notNull<SGLRender>()

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        setEGLContextClientVersion(2)
        sglRender = SGLRender(this.context)
        setRenderer(sglRender)
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        try {
            sglRender.setImage(BitmapFactory.decodeStream(
                    resources.assets.open("texture/fengj.png")))
            requestRender()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setFilter(filter: AFilter) {
        sglRender.setFilter(filter)
    }

}