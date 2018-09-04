package com.example.gl.opengles_demo.day01;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-08-31 9:55
 * desc:
 */
public abstract class AbstractMyRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "AbstractMyRenderer";

    protected float ratio;
    // 提供给外部的旋转控制
    public float xrotate = 0f;
    public float yrotate = 0f;


    /**
     * 1
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated: " + Thread.currentThread().getName());
        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    }

    /**
     * 2
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged: " + Thread.currentThread().getName());
        gl.glViewport(0, 0, width, height);
        ratio = (float) width / (float) height;

        // 投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        //设置平截头体
        gl.glFrustumf(-ratio, ratio, -1f, 1f, 3f, 7f);
    }

    /**
     * 3
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        Log.i(TAG, "onDrawFrame: " + Thread.currentThread().getName());
        onDrawFrameInit(gl);
        onDrawChildFrame(gl);
    }

    private void onDrawFrameInit(GL10 gl) {
        //清除颜色缓冲区
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
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
    }

    public abstract void onDrawChildFrame(GL10 gl);

}
