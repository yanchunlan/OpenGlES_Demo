package com.opengles.book.es2_0_test2

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.opengles.book.es2_0_test2.camera.CameraActivity
import com.opengles.book.es2_0_test2.egl.EGLActivity
import com.opengles.book.es2_0_test2.encodec.EncodecActivity
import com.opengles.book.es2_0_test2.image_video.ImgVideoActivity
import com.opengles.book.es2_0_test2.main.MenuAdapter
import com.opengles.book.es2_0_test2.main.MenuBean
import com.opengles.book.es2_0_test2.muti.MutiActivity
import com.opengles.book.es2_0_test2.push.PushActivity
import com.opengles.book.es2_0_test2.surfaceview.SurfaceViewActivity
import com.opengles.book.es2_0_test2.yuv.YuvActivity

class ES2p0_test2_MainActivity : AppCompatActivity() {

    private var data: java.util.ArrayList<MenuBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView = RecyclerView(this)
        setContentView(recyclerView)

        data = java.util.ArrayList()
        setData()
        recyclerView.run {
            layoutManager = LinearLayoutManager(this@ES2p0_test2_MainActivity,
                    LinearLayoutManager.VERTICAL,
                    false)
            adapter = MenuAdapter(data, MenuAdapter.ItemClickListener { bean ->
                startActivity(Intent(context, bean.clazz))
            })
        }
    }

    private fun setData() {
        data?.run {
            add(MenuBean("EGL环境搭建", EGLActivity::class.java))
            add(MenuBean("SurfaceView自定义", SurfaceViewActivity::class.java))
            add(MenuBean("多SurfaceView多texture绘制图片", MutiActivity::class.java))
            add(MenuBean("摄像头预览+方向适配+文字水印", CameraActivity::class.java))
            add(MenuBean("视频编码录制+音乐裁剪合成 mp4", EncodecActivity::class.java))
            add(MenuBean("图片合成视频", ImgVideoActivity::class.java))
            add(MenuBean("yvu视频数据纹理绘制", YuvActivity::class.java))
            add(MenuBean("直播推流", PushActivity::class.java))
        }
    }
}
