package com.opengles.book.es2_0.base

import android.opengl.GLSurfaceView
import android.view.View

/**
 * author: ycl
 * date: 2018-10-28 14:45
 * desc:
 */
abstract class BaseRenderer : GLSurfaceView.Renderer {
    protected val view: View

    constructor(view: View) {
        this.view = view
    }
}