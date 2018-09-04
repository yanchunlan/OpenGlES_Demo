package com.example.gl.opengles_demo.day03.fog;

import android.opengl.GLU;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-04 16:03
 * desc: 雾
 */
public class MyRendererFog extends AbstractMyRenderer {

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
//        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_DEPTH_TEST);
    }

    // 设置z轴更长
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        gl.glViewport(0, 0, width, height);
        ratio = (float) width / (float) height;

        // 投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        //设置平截头体
        gl.glFrustumf(-ratio, ratio, -1f, 1f, 3f, 200f);
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


        // 启动雾
        float[] color = {0f, 0f, 0f, 1f};
        gl.glEnable(GL10.GL_FOG);
        gl.glFogfv(GL10.GL_FOG_COLOR, BufferUtils.arr2FloatBuffer(color)); //雾颜色

        //开始于结束只针对线性雾处理,对于exp和exp2没有作用
        //exp和exp2效果需要指定雾稠密度
        gl.glFogf(GL10.GL_FOG_START, 3);//开始距离
        gl.glFogf(GL10.GL_FOG_END, 50);//结束距离
        gl.glFogf(GL10.GL_FOG_MODE, GL10.GL_EXP);//线性雾模式
        //雾稠密度:默认值1
        gl.glFogf(GL10.GL_FOG_DENSITY, 0.2f);


        // x轴向左移0.4f单位
        float r = 0.4f;
        gl.glColor4f(0, 0, 1, 1);
        gl.glTranslatef(-ratio + r, 0, 0);

        for (int i = 0; i < 10; i++) {
            BufferUtils.drawSphere(gl, 0.5f, 5, 5);
            gl.glTranslatef(0.2f, 0, 0);
            gl.glTranslatef(0, 0, -3f);

        }
    }
}
