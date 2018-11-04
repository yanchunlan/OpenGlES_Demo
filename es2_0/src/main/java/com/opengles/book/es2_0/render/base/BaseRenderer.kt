package com.opengles.book.es2_0.render.base

import android.opengl.GLES20
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

    protected fun loadShader(type: Int, shaderCode: String): Int {
        //根据type创建顶点着色器或者片元着色器
        val shader = GLES20.glCreateShader(type)
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}