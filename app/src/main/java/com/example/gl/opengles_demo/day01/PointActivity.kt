package com.example.gl.opengles_demo.day01

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import kotlin.properties.Delegates

/**
 * author: ycl
 * date: 2018-08-31 0:39
 * desc: 点
 */
class PointActivity : AppCompatActivity() {
    private var render:MyPointRenderer by Delegates.notNull<MyPointRenderer>()
    private var surfaceView:MyGlSurfaceView by Delegates.notNull<MyGlSurfaceView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surfaceView = MyGlSurfaceView(this)
        render = MyPointRenderer()
        surfaceView.setRenderer(render)
        setContentView(surfaceView)
    }

    // 设置按键控制旋转渲染的角度
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val step = 5f
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> render.xrotate = render.xrotate - step
            KeyEvent.KEYCODE_DPAD_DOWN -> render.xrotate = render.xrotate + step
            KeyEvent.KEYCODE_DPAD_LEFT -> render.yrotate = render.yrotate + step
            KeyEvent.KEYCODE_DPAD_RIGHT -> render.yrotate = render.yrotate -step
        }
        //请求渲染,和脏渲染配合使用
        surfaceView.requestRender()
        return super.onKeyDown(keyCode, event)
    }
}