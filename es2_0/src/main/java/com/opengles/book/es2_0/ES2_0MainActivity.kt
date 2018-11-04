package com.opengles.book.es2_0

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.opengles.book.es2_0.image.SGLViewActivity
import com.opengles.book.es2_0.main.MenuAdapter
import com.opengles.book.es2_0.main.MenuBean
import com.opengles.book.es2_0.render.FGLViewActivity


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
                        startActivity(Intent(context, bean.clazz)) })
        }
    }

    private fun setData() {
        data?.apply {
            add(MenuBean("绘制形体", FGLViewActivity::class.java))
            add(MenuBean("图片处理", SGLViewActivity::class.java))

        }

    }
}
