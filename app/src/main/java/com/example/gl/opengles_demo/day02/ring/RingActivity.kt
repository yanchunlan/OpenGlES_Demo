package com.example.gl.opengles_demo.day02.ring

import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer

/**
 * author: ycl
 * date: 2018-09-01 22:36
 * desc: 圆环
 */
class RingActivity: AbstractMyActivity() {
    override fun getAbstractMyRenderer(): AbstractMyRenderer = MyRingRenderer()
}