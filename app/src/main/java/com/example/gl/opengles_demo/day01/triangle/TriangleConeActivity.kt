package com.example.gl.opengles_demo.day01.triangle

import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer

/**
 * author: ycl
 * date: 2018-08-31 14:45
 * desc:
 */
class TriangleConeActivity: AbstractMyActivity() {
    override fun getAbstractMyRenderer(): AbstractMyRenderer = MyTriangleConeRenderer()
}