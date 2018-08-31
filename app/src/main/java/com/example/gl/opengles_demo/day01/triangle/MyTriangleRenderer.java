package com.example.gl.opengles_demo.day01.triangle;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-08-31 11:02
 * desc: 三角形带,正方形
 */
public class MyTriangleRenderer extends AbstractMyRenderer {
    @Override
    public void onDrawChildFrame(GL10 gl) {

        // 计算点坐标
        float r = 0.5f; // 半径
        float[] coords = {
                -r, r, 0f,
                -r, -r, 0f,
                r, r, 0f,
                r, -r, 0f,
        };

        // 转换点成为缓冲区数据
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtils.arr2ByteBuffer(coords));
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

    }
}
