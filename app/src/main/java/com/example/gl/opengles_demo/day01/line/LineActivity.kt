package com.example.gl.opengles_demo.day01.line

import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer

/**
 * author: ycl
 * date: 2018-08-31 10:43
 * desc:
 */
class LineActivity : AbstractMyActivity() {
    override fun getAbstractMyRenderer(): AbstractMyRenderer = MyLineRenderer()
}