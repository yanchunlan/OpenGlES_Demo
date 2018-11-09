package com.opengles.book.es2_0.image

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.SeekBar
import com.opengles.book.es2_0.R
import com.opengles.book.es2_0.image.filter.ContrastColorFilter
import com.opengles.book.es2_0.image.filter.Filter
import com.opengles.book.es2_0.image.filter.RGBColorFilter
import kotlinx.android.synthetic.main.activity_sglview.*
import kotlin.properties.Delegates

class SGLViewActivity : AppCompatActivity() {
    private var mGLView: SGLView by Delegates.notNull<SGLView>()
    private var isHalf = false
    private val rgbColorFilter by lazy { RGBColorFilter(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sglview)
        mGLView = findViewById<SGLView>(R.id.sglview)


        vp_rg.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.vp_rb_1 -> rgbColorFilter.setType(1)
                R.id.vp_rb_2 -> rgbColorFilter.setType(2)
                R.id.vp_rb_3 -> rgbColorFilter.setType(3)
                R.id.vp_rb_4 -> rgbColorFilter.setType(4)
            }
        }
        sb_r.setOnSeekBarChangeListener(object : SimpleSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                super.onProgressChanged(seekBar, progress, fromUser)
                mGLView.sglRender.aFilter.let {
                    if (it is RGBColorFilter) {
                        it.setR(progress / 100.0f)
                    } else {
                        mGLView.setFilter(rgbColorFilter.apply { setR(progress / 100.0f) })
                    }
                }
                tv_r.text = "$progress"
                mGLView.requestRender()
            }
        })
        sb_g.setOnSeekBarChangeListener(object : SimpleSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                super.onProgressChanged(seekBar, progress, fromUser)
                mGLView.sglRender.aFilter.let {
                    if (it is RGBColorFilter) {
                        it.setG(progress / 100.0f)
                    } else {
                        mGLView.setFilter(rgbColorFilter.apply { setG(progress / 100.0f) })
                    }
                }
                tv_g.text = "$progress"
                mGLView.requestRender()
            }
        })
        sb_b.setOnSeekBarChangeListener(object : SimpleSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                super.onProgressChanged(seekBar, progress, fromUser)
                mGLView.sglRender.aFilter.let {
                    if (it is RGBColorFilter) {
                        it.setB(progress / 100.0f)
                    } else {
                        mGLView.setFilter(rgbColorFilter.apply { setB(progress / 100.0f) })
                    }
                }
                tv_b.text = "$progress"
                mGLView.requestRender()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mGLView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mGLView.onPause()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.itemId?.run {
            when (this) {
                R.id.mDeal -> {
                    isHalf = !isHalf
                    if (isHalf) {
                        item.setTitle("处理一半")
                    } else {
                        item.setTitle("全部处理")
                    }
                    mGLView.sglRender.refresh() // 开启更新，setFilter也会开启更新
                }
                R.id.mDefault -> mGLView.setFilter(ContrastColorFilter(
                        this@SGLViewActivity, Filter.NONE))
                R.id.mGray -> mGLView.setFilter(ContrastColorFilter(
                        this@SGLViewActivity, Filter.GRAY))
                R.id.mCool -> mGLView.setFilter(ContrastColorFilter(
                        this@SGLViewActivity, Filter.COOL))
                R.id.mWarm -> mGLView.setFilter(ContrastColorFilter(
                        this@SGLViewActivity, Filter.WARM))
                R.id.mBlur -> mGLView.setFilter(ContrastColorFilter(
                        this@SGLViewActivity, Filter.BLUR))
                R.id.mMagn -> mGLView.setFilter(ContrastColorFilter(
                        this@SGLViewActivity, Filter.MAGN))
            }
        }

        mGLView.sglRender.aFilter?.setHalf(isHalf)
        mGLView.sglRender.aFilter.run {
            if (this is ContrastColorFilter) {
                rgbColorFilter.setType(filter.getvChangeType())
                rgbColorFilter.setR(filter.data[0])
                rgbColorFilter.setG(filter.data[1])
                rgbColorFilter.setB(filter.data[2])

                (vp_rg.getChildAt(filter.getvChangeType() - 1) as? RadioButton)?.isChecked = true
                filter.data.forEachIndexed { index, fl ->
                    when (index) {
                        0 -> {
                            sb_r.progress = (fl*100).toInt()
                            tv_r.text = "${(fl*100)}"
                        }
                        1 -> {
                            sb_g.progress = (fl*100).toInt()
                            tv_g.text = "${(fl*100)}"
                        }
                        2 -> {
                            sb_b.progress = (fl*100).toInt()
                            tv_b.text = "${(fl*100)}"
                        }
                    }
                }
            }
        }
        // 此处只调用一次requestRender不行，需要全部重新调用
        mGLView.requestRender()
        return super.onOptionsItemSelected(item)
    }
}
