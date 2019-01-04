package com.opengles.book.es2_0_test2

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.opengles.book.es2_0_test2.egl.EGLActivity
import com.opengles.book.es2_0_test2.main.MenuAdapter
import com.opengles.book.es2_0_test2.main.MenuBean
import com.opengles.book.es2_0_test2.muti.MutiActivity
import com.opengles.book.es2_0_test2.surfaceview.SurfaceViewActivity

class ES2p0_test2_MainActivity : AppCompatActivity() {

    private var data: java.util.ArrayList<MenuBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView = RecyclerView(this)
        setContentView(recyclerView)

        data = java.util.ArrayList()
        setData()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ES2p0_test2_MainActivity,
                    LinearLayoutManager.VERTICAL,
                    false)
            adapter = MenuAdapter(data, MenuAdapter.ItemClickListener { bean ->
                startActivity(Intent(context, bean.clazz))
            })
        }
    }

    private fun setData() {
        data?.apply {
            add(MenuBean("EGL", EGLActivity::class.java))
            add(MenuBean("SurfaceView", SurfaceViewActivity::class.java))
            add(MenuBean("多SurfaceView多texture绘制图片", MutiActivity::class.java))
        }
    }
}
