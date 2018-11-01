package com.opengles.book.es2_0.render.render;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.View;

import com.opengles.book.es2_0.base.BaseRenderer;
import com.opengles.book.es2_0.utils.BufferUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-10-30 23:57
 * desc: 颜色三角形
 */
public class TriangleColorFull extends BaseRenderer {


// 像attribute、uniform、varying都是在OpenGL的着色器语言中表示限定符，
// attribute一般用于每个顶点都各不相同的量。
// uniform一般用于对同一组顶点组成的3D物体中各个顶点都相同的量。
// varying一般用于从顶点着色器传入到片元着色器的量。
// 还有个const表示常量

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "varying vec4 vColor;" +
                    "attribute vec4 aColor;" +
                    "void main(){" +
                    "gl_Position=vMatrix*vPosition" +
                    "vColor=aColor;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float" +
                    "varying vec4 vColor;" +
                    "void main(){" +
                    "gl_FragColor=vColor;" +
                    "}";
    private FloatBuffer vertexBuffer, colorBuffer;
    private int mProgram;
    final float triangleCoords[] = {
            0.5f, 0.5f, 0f
            - 0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f
    };
    final float color[] = {
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };


    private float[] mProjectMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mVMVPMatrix = new float[16];

    private int mPositionhandler;
    private int mColorhandler;
    private int mMatrixhandler;


    public TriangleColorFull(@NotNull View view) {
        super(view);
        vertexBuffer = BufferUtils.arr2FloatBuffer(triangleCoords);
        colorBuffer = BufferUtils.arr2FloatBuffer(color);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_VERTEX_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = (float) width / height;

        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio,
                -1, 1, 3, 7);
        Matrix.setLookAtM(mViewMatrix, 0,
                0, 0, 7,
                0, 0, 0,
                0, 1, 0
        );
        Matrix.multiplyMM(mVMVPMatrix, 0,
                mProjectMatrix, 0,
                mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glUseProgram(mProgram);
        mMatrixhandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixhandler, 1, false, mVMVPMatrix, 0);

        mPositionhandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionhandler);
        GLES20.glVertexAttribPointer(mPositionhandler, 3,
                GLES20.GL_FLOAT, false,
                4 * 3, vertexBuffer);

        mColorhandler = GLES20.glGetAttribLocation(mProgram, "aColor");
        // 简单绘制使用这种方式
//        GLES20.glUniform4fv(mColorhandler, 1, colorBuffer);

        // 绘制多种需要设置到point中,只要使用到顶点缓冲区都需要开启缓冲区
        GLES20.glEnableVertexAttribArray(mColorhandler);
        GLES20.glVertexAttribPointer(mColorhandler, 4,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer); // 颜色是没有偏移量的

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, triangleCoords.length / 3);
        GLES20.glDisableVertexAttribArray(mPositionhandler);
    }
}
