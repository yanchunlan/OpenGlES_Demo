package com.opengles.book.es2_0.render

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.opengles.book.es2_0.R
import com.opengles.book.es2_0.base.BaseRenderer
import com.opengles.book.es2_0.base.MyRenderer
import kotlinx.android.synthetic.main.activity_fglview.*

class FGLViewActivity : AppCompatActivity() {
    companion object {
        private val REQ_CHOOSE: Int = 0x0101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fglview)

        mChange.setOnClickListener {
            startActivityForResult(Intent(this, ChooseActivity::class.java), REQ_CHOOSE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CHOOSE && resultCode == Activity.RESULT_OK) {
            mGLView.setRendererClass(data?.getSerializableExtra(MyRenderer.PARAMS) as Class<out BaseRenderer>)
        }
    }


    override fun onResume() {
        super.onResume()
        mGLView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mGLView.onPause()
    }
}
