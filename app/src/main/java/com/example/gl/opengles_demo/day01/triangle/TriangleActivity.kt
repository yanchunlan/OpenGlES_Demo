package com.example.gl.opengles_demo.day01.triangle

import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer

/**
 * author: ycl
 * date: 2018-08-31 11:02
 * desc: 三角形带,正方形
 */
class TriangleActivity : AbstractMyActivity() {
    override fun getAbstractMyRenderer(): AbstractMyRenderer = MyTriangleRenderer()
}