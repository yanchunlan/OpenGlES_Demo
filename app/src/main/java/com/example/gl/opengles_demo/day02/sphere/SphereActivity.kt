package com.example.gl.opengles_demo.day02.sphere

import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer

/**
 * author: ycl
 * date: 2018-09-01 18:02
 * desc: 球
 */
class SphereActivity : AbstractMyActivity() {
    override fun getAbstractMyRenderer(): AbstractMyRenderer = MySphereRenderer()
}