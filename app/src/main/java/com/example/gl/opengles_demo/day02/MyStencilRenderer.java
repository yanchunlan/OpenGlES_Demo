package com.example.gl.opengles_demo.day02;

import android.opengl.GLU;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-01 10:39
 * desc:
 * 模板:使用模板缓冲区,
 * 相当于使用矩阵实现绘制不同的物体.动画效果 使用模板缓冲区需要硬件支持GPU编程才可以.
 */
public class MyStencilRenderer extends AbstractMyRenderer {

    List<Float> vertexList;
    float left = -ratio, top = 1f, width = 0.3f;
    boolean xadd = false;
    boolean yadd = false;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        super.onSurfaceCreated(gl, config);
        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glClearStencil(0);// 模板清除值

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_STENCIL_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        super.onSurfaceChanged(gl, width, height);
        gl.glViewport(0, 0, width, height);
        ratio = (float) width / (float) height;
        left = -ratio;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
//        super.onDrawFrame(gl);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_STENCIL_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        GLU.gluLookAt(gl, 0, 0, 5, 0, 0, 0, 0, 1, 0);

        /****************************** 绘制白色螺旋线 **********************************/
        gl.glPushMatrix(); // 储存现在变换之前的值，存栈， glPopMatrix
        gl.glRotatef(xrotate, 1, 0, 0);
        gl.glRotatef(yrotate, 0, 1, 0);

        gl.glShadeModel(GL10.GL_FLAT);// 单调着色

        vertexList = new ArrayList<>();
        float x = 0f, y = 0.8f, z = 0f, ystep = 0.005f, r = 0.7f;
        for (float angle = 0f; angle < (Math.PI * 2 * 3); angle += (Math.PI / 40)) {
            x = (float) (r * Math.cos(angle));
            z = -(float) (r * Math.sin(angle));
            y -= ystep;
            vertexList.add(x);
            vertexList.add(y);
            vertexList.add(z);
        }
        FloatBuffer fvb = BufferUtils.list2FloatBuffer(vertexList);

        // 绘制白线直线,设置模板函数,所有操作都不能通过测试,但是对模板缓冲区的值进行增加
        gl.glStencilFunc(GL10.GL_NEVER, 0, 0);
        gl.glStencilOp(GL10.GL_INCR, GL10.GL_INCR, GL10.GL_INCR); // 升序

        // 绘制白色螺旋线
        gl.glColor4f(1f, 1f, 1f, 1f);// 白线
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, fvb);
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, vertexList.size() / 3);
        gl.glPopMatrix();


        /****************************** 绘制红色方块 **********************************/
        if (xadd) {
            left = left + 0.01f;
        } else {
            left = left - 0.01f;
        }
        if (left <= (-ratio)) {
            xadd = true;
        }
        if (left >= (ratio - width)) {
            xadd = false;
        }

        if (yadd) {
            top = top + 0.01f;
        } else {
            top = top - 0.01f;
        }
        if (top >= 1) {
            yadd = false;
        }
        if (top <= (-1 + width)) {
            yadd = true;
        }

        float[] rectVertex = { left, top - width, 2f, left, top, 2f,
                left + width, top - width, 2f, left + width, top, 2f };
        FloatBuffer rectfb = BufferUtils.arr2FloatBuffer(rectVertex);

        // 设置模板函数,所有操作都不能通过测试,但是对模板缓冲区的值进行增加
        // 此处设置0x1 是固定值
        gl.glStencilFunc(GL10.GL_NOTEQUAL, 0x1, 0x1);
        gl.glStencilOp(GL10.GL_KEEP, GL10.GL_KEEP, GL10.GL_KEEP);

        gl.glColor4f(1f, 0, 0, 1);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, rectfb);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);// 三角形代
    }


    @Override
    public void onDrawChildFrame(GL10 gl) {
    }
}
