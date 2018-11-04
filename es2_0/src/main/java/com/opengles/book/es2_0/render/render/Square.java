package com.opengles.book.es2_0.render.render;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.View;

import com.opengles.book.es2_0.render.base.BaseRenderer;
import com.opengles.book.es2_0.utils.BufferUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-10-31 0:35
 * desc: 正方形
 */
public class Square extends BaseRenderer {

    private String vertexShaderCodes =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "void main(){" +
                    "gl_Position=vMatrix*vPosition;" +
                    "}";
    // uniform 用于每个地方都一样的地方
    private String fragmentShaderCodes =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    "gl_FragColor=vColor;" +
                    "}";

    private final float[] vertexCoords = {
            -0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f
    };
    private final short[] index = {0,1,2,0,2,3};// 逆时针顶点

    private final float[] color = {1f, 1f, 0f, 1f};

    private FloatBuffer vertexBuffer;
    private ShortBuffer shortBuffer;


    private int mProgram;
    private float[] projectMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private int positionHandler;
    private int colorHandler;
    private int matrixHandler;


    public Square(@NotNull View view) {
        super(view);

        vertexBuffer = BufferUtils.arr2FloatBuffer(vertexCoords);
        shortBuffer = BufferUtils.arr2ShortBuffer(index);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCodes);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCodes);


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
        Matrix.frustumM(projectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 7, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glUseProgram(mProgram);

        matrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(matrixHandler, 1, false, mvpMatrix, 0);

        positionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandler);
        // 准备三角形的坐标数据
        GLES20.glVertexAttribPointer(positionHandler, 3,
                GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);

        colorHandler = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandler, 1, color, 0);


        // 绘制三角形
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCoords.length / 3);
        // 索引法绘制正方形
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                index.length, // 多少个顶点
                GLES20.GL_UNSIGNED_SHORT, // GL_UNSIGNED_SHORT 与SHORT 区别是：
                shortBuffer);
        GLES20.glDisableVertexAttribArray(positionHandler);

        ///  GL_TRIANGLES  三角形
        ///  GL_TRIANGLE_FAN  扇面绘制
        ///  GL_TRIANGLE_STRIP  绘制连续的三角形

    }
}
