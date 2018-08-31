package com.example.gl.opengles_demo.day01.scissor

import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer

/**
 * author: ycl
 * date: 2018-08-31 17:00
 * desc:
 */
class ScissorActivity: AbstractMyActivity() {
    override fun getAbstractMyRenderer(): AbstractMyRenderer = MyScissorRenderer()
}