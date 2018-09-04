package com.example.gl.opengles_demo.day01

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import kotlin.properties.Delegates

/**
 * author: ycl
 * date: 2018-08-31 11:22
 * desc:
 */
abstract class AbstractMyActivity : AppCompatActivity() {

    protected var render: AbstractMyRenderer by Delegates.notNull<AbstractMyRenderer>()
    protected var surfaceView: MyGlSurfaceView by Delegates.notNull<MyGlSurfaceView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (abstractInit()) {
            surfaceView = MyGlSurfaceView(this)
            render = getAbstractMyRenderer()

            surfaceView.setRenderer(render)

            //GLSurfaceView.RENDERMODE_CONTINUOUSLY:持续渲染(默认)
            //GLSurfaceView.RENDERMODE_WHEN_DIRTY:脏渲染,命令渲染
            surfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY// 只渲染一次

            setContentView(surfaceView)
        }
    }


    override fun onResume() {
        super.onResume()
        surfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        surfaceView.onPause()
    }
    // 设置按键控制旋转渲染的角度
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val step = 5f
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> render.xrotate = render.xrotate - step
            KeyEvent.KEYCODE_DPAD_DOWN -> render.xrotate = render.xrotate + step
            KeyEvent.KEYCODE_DPAD_LEFT -> render.yrotate = render.yrotate + step
            KeyEvent.KEYCODE_DPAD_RIGHT -> render.yrotate = render.yrotate - step
        }
        //请求渲染,和脏渲染配合使用
        surfaceView.requestRender()
        return super.onKeyDown(keyCode, event)
    }

    protected abstract fun getAbstractMyRenderer(): AbstractMyRenderer

    open fun abstractInit(): Boolean = true

}