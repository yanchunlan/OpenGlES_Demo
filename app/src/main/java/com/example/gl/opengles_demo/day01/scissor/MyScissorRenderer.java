package com.example.gl.opengles_demo.day01.scissor;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-08-31 17:00
 * desc: 剪裁区
 */
public class MyScissorRenderer extends AbstractMyRenderer {

    private int width;
    private int height;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        super.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawChildFrame(GL10 gl) {

        // 启用剪裁
        gl.glEnable(GL10.GL_SCISSOR_TEST);

        // 计算点坐标 ( c, d 置位是为了解决没有绘制完全的问题)
        float[] coords = {
                -ratio, 1f, 2f,
                -ratio, -1f, 2f,
                ratio, 1f, 2f,
                ratio, -1f, 2f,
        };
        // 颜色数组（随便自定义的6中颜色）
        float[][] colors = {
                {1f, 0f, 0f, 1f},
                {0f, 1f, 0f, 1f},
                {0f, 0f, 1f, 1f},
                {1f, 1f, 0f, 1f},
                {0f, 1f, 1f, 1f},
                {1f, 0f, 1f, 1f},
        };

        int step = 40;
        for (int i = 0; i < colors.length; i++) {
            //设置剪裁区 (此处的20是随意定的，必须宽度高度大于其值才行)
            gl.glScissor(i * step, i * step, width - (i * step * 2), height - (i * step * 2));

            //设置颜色
            gl.glColor4f(colors[i][0], colors[i][1], colors[i][2], colors[i][3]);
            // 转换点成为缓冲区数据
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtils.arr2ByteBuffer(coords));
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        }
    }
}
