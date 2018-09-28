package com.example.gl.opengles_demo.gles_2p0;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.gl.opengles_demo.gles_2p0.shape.square.Square;
import com.example.gl.opengles_demo.gles_2p0.shape.triangle.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-25 17:22
 * desc:
 * Vertex Shader - 用于渲染形状的顶点的OpenGLES 图形代码。
 * Fragment Shader - 用于渲染形状的外观（颜色或纹理）的OpenGLES 代码。
 * Program - 一个OpenGLES对象，包含了你想要用来绘制一个或多个形状的shader。
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Triangle mTriangle;
    private Square mSquare;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 1);

        mTriangle = new Triangle();
        mSquare = new Square();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mTriangle.draw();
    }


    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


}
