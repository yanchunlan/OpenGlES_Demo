package com.example.gl.opengles_demo.day02.cube

import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer

/**
 * author: ycl
 * date: 2018-09-01 23:50
 * desc: 立方体
 */
class CubeActivity : AbstractMyActivity() {
    override fun getAbstractMyRenderer(): AbstractMyRenderer = MyCubeRenderer()
}