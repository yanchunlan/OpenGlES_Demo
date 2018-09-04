package com.example.gl.opengles_demo.day02.sphere;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-01 16:51
 * desc: 球
 */
public class MySphereRenderer extends AbstractMyRenderer {
    @Override
    public void onDrawChildFrame(GL10 gl) {
        BufferUtils.drawSphere(gl,.5f,6,8);
    }
}
