package com.example.gl.opengles_demo.day02.light

import android.app.Dialog
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer
import com.example.gl.opengles_demo.day01.MyGlSurfaceView
import kotlin.properties.Delegates


/**
 * author: ycl
 * date: 2018-09-02 18:33
 * desc: 光照
 */
class LightActivity : AbstractMyActivity() {
    private var d: Dialog by Delegates.notNull<Dialog>()

    override fun abstractInit(): Boolean = false

    override fun getAbstractMyRenderer(): AbstractMyRenderer = getAbstractMyLightRenderer()
    private fun getAbstractMyLightRenderer(): AbstractMyLightRenderer = MyLightRenderer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        surfaceView = MyGlSurfaceView(this)
        render = getAbstractMyLightRenderer()
        surfaceView.setRenderer(render)
        surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        setContentView(surfaceView)

        buildDialog()
    }

    private fun buildDialog() {
        d = LightDialog(this, render)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("设置")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (d.isShowing.not()) {
            d.show()
        }
        return super.onOptionsItemSelected(item)
    }
}