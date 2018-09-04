package com.example.gl.opengles_demo.day03.antalias

import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer

/**
 * author: ycl
 * date: 2018-09-04 16:45
 * desc:
 */
class AntliaActivity : AbstractMyActivity() {
    override fun getAbstractMyRenderer(): AbstractMyRenderer = MyRendererAntiAlias()
}