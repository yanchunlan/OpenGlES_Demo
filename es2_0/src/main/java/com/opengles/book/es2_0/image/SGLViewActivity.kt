package com.opengles.book.es2_0.image

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.opengles.book.es2_0.R
import com.opengles.book.es2_0.image.filter.ContrastColorFilter
import com.opengles.book.es2_0.image.filter.Filter
import kotlin.properties.Delegates

class SGLViewActivity : AppCompatActivity() {
    private var mGLView: SGLView by Delegates.notNull<SGLView>()
    private var isHalf = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGLView = SGLView(this)
        setContentView(mGLView)
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
                    /* isHalf = !isHalf
                     if (isHalf) {
                         item.setTitle("处理一半")
                     } else {
                         item.setTitle("全部处理")
                     }
                     mGLView.getRender().refresh()*/
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
        mGLView.requestRender()
        return super.onOptionsItemSelected(item)
    }
}
