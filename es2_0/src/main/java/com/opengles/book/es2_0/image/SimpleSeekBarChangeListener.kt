package com.opengles.book.es2_0.image

import android.widget.SeekBar

/**
 * @Author:  ycl
 * @Date:  2018/11/8 21:28
 * @Desc:
 */
open abstract class SimpleSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}