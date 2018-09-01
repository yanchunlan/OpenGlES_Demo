package com.example.gl.opengles_demo.day02.cube;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-01 23:50
 * desc: 立方体渲染器,线代模式,正交投影
 */
public class MyCubeRenderer extends AbstractMyRenderer {


    /**
     * 去除平截头体，修改为正交投影
     * gl.glOrthof
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        super.onSurfaceChanged(gl, width, height);

        gl.glViewport(0, 0, width, height);
        ratio = (float) width / (float) height;
        // 投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        //正交投影
        gl.glOrthof(-ratio, ratio, -1f, 1f, 3f, 7f);
    }

    @Override
    public void onDrawChildFrame(GL10 gl) {
        float r = 0.4f;
        // 定义坐标中心在正方体中心
        float[] coords = {
                -r, r, r,
                -r, -r, r,
                r, r, r,
                r, -r, r,

                -r, r, -r,
                -r, -r, -r,
                r, r, -r,
                r, -r, -r,
        };
        // 顶点的索引顺序
        byte[] indices = {
                0, 1, 5, 4, 0,
                0, 4, 6, 2,
                6, 7, 3,
                7, 5, 1,
                0, 2, 3, 1
        };

        // 存入缓冲区
        gl.glVertexPointer(3,
                GL10.GL_FLOAT,
                0,
                BufferUtils.arr2FloatBuffer(coords));
        // 此处不能使用 drawArray 了，因为需要顶点来连接
        gl.glDrawElements(GL10.GL_LINE_STRIP,
                indices.length, // 索引数量
                GL10.GL_UNSIGNED_BYTE,
                BufferUtils.arr2ByteBuffer(indices));
    }
}
