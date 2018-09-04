package com.example.gl.opengles_demo.day03.light

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.gl.opengles_demo.day01.AbstractMyActivity
import com.example.gl.opengles_demo.day01.AbstractMyRenderer
import kotlin.properties.Delegates

/**
 * author: ycl
 * date: 2018-09-03 15:41
 * desc:
 */

class LightReviewActivity : AbstractMyActivity() {

    private var d: Dialog by Delegates.notNull<Dialog>()

    override fun getAbstractMyRenderer(): AbstractMyRenderer = getAbstractMyLightRenderer1()
    private fun getAbstractMyLightRenderer1(): MyRendererLighting1 = MyRendererLighting1()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            d = LightDialog1(this, render as MyRendererLighting1,surfaceView)

            // 测试
           /* val clazz = R.id::class.java
            val fs = clazz.declaredFields
            val fs1 = clazz.fields
            Log.i(javaClass.name, "fs:" + fs.size+"fs1:"+fs1.size)*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add("设置")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
       /* if (d.isShowing.not()) {
            d.show()
        }*/
        return super.onOptionsItemSelected(item)
    }
}