package com.example.gl.opengles_demo.day03.tex

import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer

/**
 * author: ycl
 * date: 2018-09-04 17:33
 * desc:
 */
class TexActivity: AbstractMyActivity() {
    override fun getAbstractMyRenderer(): AbstractMyRenderer = MyRendererTex(resources)
}