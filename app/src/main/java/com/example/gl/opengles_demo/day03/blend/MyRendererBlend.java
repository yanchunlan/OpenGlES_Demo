package com.example.gl.opengles_demo.day03.blend;

import android.opengl.GLU;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-04 16:03
 * desc:
 */
public class MyRendererBlend extends AbstractMyRenderer {

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_DEPTH_TEST);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
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

        onDrawChildFrame(gl);
    }

    @Override
    public void onDrawChildFrame(GL10 gl) {

        gl.glShadeModel(GL10.GL_FLAT);
        //启用混合
        gl.glEnable(GL10.GL_BLEND);
        //设置混合因子,openGL ES指定混合方程式,用户混合方程式是GL_FUNC_ADD
        gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);

        // 后面正方形(红色)  w = h =0.7f
        float[] back = {
                -0.2f, -0.2f, -1,
                0.5f, -0.2f, -1,
                -0.2f, 0.5f, -1,
                0.5f, 0.5f, -1,
        };
        gl.glColor4f(1f, 0f, 0f, 1);
        BufferUtils.drawRect(gl, back);

        //后面正方形(蓝色)
        float[] front = {
                -0.6f, -0.6f, 1,
                0.2f, -0.6f, 1,
                -0.6f, 0.2f, 1,
                0.2f, 0.2f, 1,
        };
        gl.glColor4f(0f, 0f, 1f, 1);
        BufferUtils.drawRect(gl, front);
    }
}
