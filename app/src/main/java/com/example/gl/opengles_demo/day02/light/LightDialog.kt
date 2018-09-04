package com.example.gl.opengles_demo.day02.light

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.SeekBar
import com.example.gl.opengles_demo.R
import com.example.gl.opengles_demo.day01.AbstractMyRenderer
import org.jetbrains.anko.findOptional



/**
 * author: ycl
 * date: 2018-09-03 0:38
 * desc:
 */
class LightDialog(context: Context?,
                  val render: AbstractMyRenderer) :
        Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.light_set)
        addCheckboxEvent()
    }

    //添加复选框事件
    private fun addCheckboxEvent() {
        val r: Resources = this.context.resources
        //box监听
        val cbOnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            try {
                val id = buttonView.id
                var name = r.getResourceName(id)
                name = name.substring(name.lastIndexOf("_") + 1)

                // 此处不知是修改哪一个值，所以需要用反射修改最好
                val clazz = render::class.java
                val f = clazz.getField(name) // getField 只能取public数据，切父类子类数据都可以
                // getDeclaredField则限制更多，需要准确的指向
                f.isAccessible = true
                f.set(render, isChecked)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        //进度改变
        val sbOnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                try {
                    var scale = progress as Float / seekBar?.max as Float
                    val id = seekBar.id
                    var name = r.getResourceName(id)
                    name = name.substring(name.lastIndexOf("_") + 1)

                    // 此处不知是修改哪一个值，所以需要用反射修改最好
                    val clazz = render::class.java
                    val f = clazz.getField(name) // getField 只能取public数据
                    f.isAccessible = true
                    // 控制位置在 ： -10 ~ 10 之间, 原值在0~1之间 ，所以为: 20*scale-10f
                    if (name.contains("pos")) {
                        scale = -10f + (20 * scale)
                     }
                    f.set(render, scale)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        }
        try {
            // 从所有的id中取出数据
            val clazz = R.id::class.java
            val fs = clazz.declaredFields
//            val fs1 = clazz.fields
//            Log.i(javaClass.name, "fs:" + fs.size+"fs1:"+fs1.size)
            for (f in fs) {
                val id = f.get(null) as Int
                val cb = this.findOptional<CheckBox>(id)
                if (null != cb) {
                    cb.setOnCheckedChangeListener(cbOnCheckedChangeListener)
                } else {
                    val sb = this.findOptional<SeekBar>(id)
                    sb?.setOnSeekBarChangeListener(sbOnSeekBarChangeListener)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}