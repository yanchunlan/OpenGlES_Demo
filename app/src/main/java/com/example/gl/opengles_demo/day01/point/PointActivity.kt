package com.example.gl.opengles_demo.day01.point

import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer

/**
 * author: ycl
 * date: 2018-08-31 0:39
 * desc: 点
 */
class PointActivity : AbstractMyActivity() {
    override fun getAbstractMyRenderer(): AbstractMyRenderer = MyPointRenderer()
}