package com.opengles.book.es2_0.render

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.opengles.book.es2_0.base.MyRenderer
import com.opengles.book.es2_0.main.MenuAdapter
import com.opengles.book.es2_0.main.MenuBean
import com.opengles.book.es2_0.render.render.Triangle

class ChooseActivity : AppCompatActivity() {
    private var data: java.util.ArrayList<MenuBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView = RecyclerView(this)
        setContentView(recyclerView)

        data = java.util.ArrayList()
        setData()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChooseActivity,
                    LinearLayoutManager.VERTICAL,
                    false)
            adapter = MenuAdapter(data, MenuAdapter.ItemClickListener { bean ->
                context.startActivity(Intent(context, bean.clazz))
                val intent = Intent()
                intent.putExtra(MyRenderer.PARAMS, bean.clazz)
                setResult(Activity.RESULT_OK, intent)
                finish()
            })
        }
    }

    private fun setData() {
        data?.apply {
            add(MenuBean("三角形", Triangle::class.java))
            add(MenuBean("三角形", Triangle::class.java))
            add(MenuBean("三角形", Triangle::class.java))
            add(MenuBean("三角形", Triangle::class.java))
            add(MenuBean("三角形", Triangle::class.java))
        }
    }
}
