package com.example.gl.opengles_demo.day01;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-08-30 23:22
 * desc: 点渲染器,绘制螺旋线
 */
public class MyPointRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "MyPointRenderer";
    private float ratio;
    // 提供给外部的旋转控制
    public float xrotate = 0f;
    public float yrotate = 0f;


    /**
     * 1
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated: ");
        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    }

    /**
     * 2
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged: ");
        gl.glViewport(0, 0, width, height);
        ratio = (float) width / (float) height;

        // 投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        //设置平截头体
        gl.glFrustumf(-ratio, ratio,-1f, 1f,  3f, 7f);
    }

    /**
     * 3
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        Log.i(TAG, "onDrawFrame: ");
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
        gl.glRotatef(xrotate,1f,0,0);
        gl.glRotatef(yrotate,0,1f,0);


        // 计算点坐标
        float r = 0.5f; // 半径
        List<Float> coordsList = new ArrayList<>();
        float x = 0f, y = 0f, z = 1f, zStep = 0.01f;
        // 3圈6pi , z初始值为1，慢慢减小
        for (float alpha = 0f; alpha < Math.PI * 6; alpha = (float) (alpha + Math.PI / 16)) {
            x = (float) (r * Math.cos(alpha));
            y = (float) (r * Math.sin(alpha));
            z = z - zStep;
            coordsList.add(x);
            coordsList.add(y);
            coordsList.add(z);
        }

        // 转换点成为缓冲区数据
        ByteBuffer ibb = ByteBuffer.allocateDirect(coordsList.size() * 4);
        ibb.order(ByteOrder.nativeOrder());
        FloatBuffer fbb = ibb.asFloatBuffer();
        for (float f : coordsList) {
            fbb.put(f);
        }
        fbb.position(0);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, ibb);
        gl.glDrawArrays(GL10.GL_POINTS, 0, coordsList.size() / 3);
    }
}
