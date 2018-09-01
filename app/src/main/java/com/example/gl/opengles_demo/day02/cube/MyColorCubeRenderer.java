package com.example.gl.opengles_demo.day02.cube;

import android.opengl.GLU;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-02 0:40
 * desc: 颜色立方体
 */
public class MyColorCubeRenderer extends AbstractMyRenderer {

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);    //颜色缓冲区
        gl.glEnableClientState(GL10.GL_DEPTH_TEST);     //启用深度测试
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        /*****************  init  start **********************/
        //清除颜色缓冲区
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        //设置绘图颜色
        gl.glColor4f(1f, 0f, 0f, 1f);

        //操作模型视图矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        //设置眼球的参数
        GLU.gluLookAt(gl, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f);

        // 旋转角度   绕x顺时针旋转90度
        gl.glRotatef(xrotate, 1f, 0, 0);
        gl.glRotatef(yrotate, 0, 1f, 0);
        /*****************  init  end   **********************/

        float r = 0.4f;
        // 定义坐标中心在正方体中心  --顺序是 ：0123 4567
        float[] coords = {
                -r, r, r,//front left up
                -r, -r, r,//front left up
                r, r, r,//front right up
                r, -r, r,//front right up

                -r, r, -r, //back left up
                -r, -r, -r,//back let bottom
                r, r, -r,//back right up
                r, -r, -r,//back right bottom
        };
        // 顶点的索引顺序 --  前后左右上下
        // 从正方形看是逆时针就是同一个顺序,此处的三角形是随便组的，只要按照一个顺序环绕即可
        byte[] indices = {
                0,1,2,2,1,3,//front
                4,5,6,6,5,7,//back
                0,1,4,4,1,5,//left
                2,3,6,6,3,7,//right
                4,0,2,4,2,6,//top
                5,1,3,5,3,7//bottom
        };

        // 01234567 按照点大小设置颜色大小
        float[] colors = {
                0f, 1f, 1f, 1f,//青色
                0, 1, 0, 1,
                1, 1, 1, 1,//白色
                1, 1, 0, 1,//黄色
                0, 0, 1, 1,//4
                0, 0, 0, 1,//5
                1, 0, 1, 1,//6
                1, 0, 0, 1//7
        };

        // 存入颜色缓冲区  （size agba 默认一般是4）
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, BufferUtils.arr2FloatBuffer(colors));
        // 存入顶点缓冲区
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtils.arr2FloatBuffer(coords));
        // 此处不能使用 drawArray 了，因为需要顶点来连接
        gl.glDrawElements(GL10.GL_TRIANGLES,
                indices.length, // 索引数量
                GL10.GL_UNSIGNED_BYTE,
                BufferUtils.arr2ByteBuffer(indices));
    }

    @Override
    public void onDrawChildFrame(GL10 gl) {
    }
}
