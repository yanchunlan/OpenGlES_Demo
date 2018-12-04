package com.opengles.book.es2_0

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.opengles.book.es2_0.beautiful.BeautyActivity
import com.opengles.book.es2_0.camera.camera1.CameraActivity
import com.opengles.book.es2_0.camera.camera2.Camera2Activity
import com.opengles.book.es2_0.camera.camera3.Camera3Activity
import com.opengles.book.es2_0.egl.EGLActivity
import com.opengles.book.es2_0.etc.ZipActivity
import com.opengles.book.es2_0.fbo.FBOActivity
import com.opengles.book.es2_0.image.SGLViewActivity
import com.opengles.book.es2_0.main.MenuAdapter
import com.opengles.book.es2_0.main.MenuBean
import com.opengles.book.es2_0.render.FGLViewActivity
import com.opengles.book.es2_0.vary.VaryingActivity
import com.opengles.book.es2_0.vr.VRActivity
import com.opengles.book.es2_0.vr_video.VRVideoActivity


class ES2_0MainActivity : AppCompatActivity() {

    private var data: java.util.ArrayList<MenuBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView = RecyclerView(this)
        setContentView(recyclerView)

        data = java.util.ArrayList()
        setData()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ES2_0MainActivity,
                    LinearLayoutManager.VERTICAL,
                    false)
            adapter = MenuAdapter(data, MenuAdapter.ItemClickListener { bean ->
                startActivity(Intent(context, bean.clazz))
            })
        }
    }

    private fun setData() {
        data?.apply {
            add(MenuBean("绘制形体(render)", FGLViewActivity::class.java))
            add(MenuBean("图片滤镜(image)", SGLViewActivity::class.java))
            add(MenuBean("图片美颜(beautiful)", BeautyActivity::class.java))
            add(MenuBean("图形变换(vary)", VaryingActivity::class.java))
            add(MenuBean("相机(camera)", CameraActivity::class.java))
            add(MenuBean("相机动画(camera2)", Camera2Activity::class.java))
            add(MenuBean("相机美颜(camera3)", Camera3Activity::class.java))
            add(MenuBean("压缩纹理动画(etc)", ZipActivity::class.java))
            add(MenuBean("FBO离屏渲染(fbo)", FBOActivity::class.java))
            add(MenuBean("EGL后台处理(egl环境搭建)", EGLActivity::class.java))
            add(MenuBean("VR球形效果(vr)", VRActivity::class.java))
            add(MenuBean("VR球形效果(vr_video)", VRVideoActivity::class.java))
        }
    }
}
