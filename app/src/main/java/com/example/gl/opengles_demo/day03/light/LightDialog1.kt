package com.example.gl.opengles_demo.day03.light

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.SeekBar
import com.example.gl.opengles_demo.R
import com.example.gl.opengles_demo.day01.MyGlSurfaceView
import org.jetbrains.anko.findOptional

/**
 * author: ycl
 * date: 2018-09-03 15:47
 * desc:
 */
class LightDialog1(context: Context, val renderer: MyRendererLighting1, val surfaceView: MyGlSurfaceView)
    : Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.light_set1)
        addCheckboxEvent()
    }

    private fun addCheckboxEvent() {
        try {
            val clazz = R.id::class.java
            val f = clazz.fields // declaredFields
            Log.i(javaClass.name, "fs:" + f.size)
            f.forEach {
                val id = it.get(null) as Int
                val cb = this.findOptional<CheckBox>(id)
                if (null != cb) {
                    cb.setOnCheckedChangeListener(onCheckedChangeListener)
                } else {
                    val sb = this.findOptional<SeekBar>(id)
                    sb?.setOnSeekBarChangeListener(onSeekBarChangeListener)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        try {
            when (buttonView.id) {
            //启用光照
                R.id.cbEnableLighting -> renderer.enable_lighting = isChecked
            //启用颜色追踪
                R.id.cbColorMaterrial -> renderer.enable_color_material = isChecked
            //启用重算法线规范化
                R.id.cbRescaleNormals -> renderer.enable_rescale_normals = isChecked
            }
            surfaceView.requestRender()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            val percent = progress as Float / seekBar?.max as Float
            when (seekBar.id) {
            /***************  全局环境光  *************/
                R.id.bar_glbal_ambient_r -> renderer.global_ambient_r = percent
                R.id.bar_glbal_ambient_g -> renderer.global_ambient_g = percent
                R.id.bar_glbal_ambient_b -> renderer.global_ambient_b = percent
            /***************  材料的环境光*散射光的反射率  *************/
            //环境和散射光   反射率
                R.id.bar_material_ambient_and_diffuse_r -> renderer.material_ambient_and_diffuse_r = percent
                R.id.bar_material_ambient_and_diffuse_g -> renderer.material_ambient_and_diffuse_g = percent
                R.id.bar_material_ambient_and_diffuse_b -> renderer.material_ambient_and_diffuse_b = percent
            //镜面光反射率
                R.id.bar_material_specular_r -> renderer.material_specular_r = percent
                R.id.bar_material_specular_g -> renderer.material_specular_g = percent
                R.id.bar_material_specular_b -> renderer.material_specular_b = percent
            /***************  color设置  *************/
                R.id.bar_color_r -> renderer.color_r = percent
                R.id.bar_color_g -> renderer.color_g = percent
                R.id.bar_color_b -> renderer.color_b = percent
            /***************  光源0  *************/
            //环境光
                R.id.bar_light0_ambient_r -> renderer.light0_ambient_r = percent
                R.id.bar_light0_ambient_g -> renderer.light0_ambient_g = percent
                R.id.bar_light0_ambient_b -> renderer.light0_ambient_b = percent
            //闪射光
                R.id.bar_light0_diffuse_r -> renderer.light0_diffuse_r = percent
                R.id.bar_light0_diffuse_g -> renderer.light0_diffuse_g = percent
                R.id.bar_light0_diffuse_b -> renderer.light0_diffuse_b = percent
            //镜面光
                R.id.bar_light0_specular_r -> renderer.light0_specular_r = percent
                R.id.bar_light0_specular_g -> renderer.light0_specular_g = percent
                R.id.bar_light0_specular_b -> renderer.light0_specular_b = percent
            //位置  因为此处max=20，所以直接-10即可
                R.id.bar_light0_pos_x -> renderer.light0_pos_x = -10+progress as Float
                R.id.bar_light0_pos_y -> renderer.light0_pos_y = -10+progress as Float
                R.id.bar_light0_pos_z -> renderer.light0_pos_z = -10+progress as Float
            }
            surfaceView.requestRender()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }
}