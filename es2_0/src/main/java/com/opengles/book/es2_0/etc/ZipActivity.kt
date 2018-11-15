package com.opengles.book.es2_0.etc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class ZipActivity : AppCompatActivity() {

    lateinit var zipSurfaceView: ZipGlSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zipSurfaceView = ZipGlSurfaceView(this)
        setContentView(zipSurfaceView)
        // 主要是采用etc方式，对压缩的zip动画文件进行逐针动画

    }
}
