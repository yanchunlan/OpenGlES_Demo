package com.example.gl.opengles_demo.day02.cube

import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer

/**
 * author: ycl
 * date: 2018-09-02 0:39
 * desc: 颜色立方体
 */
class ColorCubeActivity : AbstractMyActivity() {
    override fun getAbstractMyRenderer(): AbstractMyRenderer = MyColorCubeRenderer()
}