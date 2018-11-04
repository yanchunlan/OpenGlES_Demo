package com.opengles.book.es2_0.image

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.opengles.book.es2_0.R
import kotlin.properties.Delegates

class SGLViewActivity : AppCompatActivity() {
    private var mGLView: SGLView by Delegates.notNull<SGLView>()

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
                R.id.mDefault ->{}
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
