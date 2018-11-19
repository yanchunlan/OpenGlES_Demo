package com.opengles.book.es2_0.etc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * 使用openGlES2.0得ETC压缩纹理动画，显示帧动画
 */
class ZipActivity : AppCompatActivity() {

    lateinit var zipSurfaceView: ZipGlSurfaceView
    private val path: String = "assets/etczip/bg.zip"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zipSurfaceView = ZipGlSurfaceView(this)
        setContentView(zipSurfaceView)
        // 主要是采用etc方式，对压缩的zip动画文件进行逐针动画

        zipSurfaceView.run {
            setOnClickListener {
                if (!this.isPlay) {
                    setAnimation(path, 50)
                    start()
                }
            }
            setChangeListener { lastState, nowState ->
                if (nowState == StateChangeListener.STOP) {
                    if (!this.isPlay) {
                        setAnimation(path, 50)
                        start()
                    }
                }
            }
        }

    }
}
