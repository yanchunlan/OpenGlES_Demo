package com.example.gl.opengles_demo.gles_2p0.shape.square;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.gl.opengles_demo.gles_2p0.MyGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * author: ycl
 * date: 2018-09-25 17:34
 * desc: 正方形（由2个三角形组成）
 */
public class Square {
    private FloatBuffer fbb;
    private ShortBuffer indexBuffer;

    static final int COORDS_PER_VERTEX = 3;

    static float squareCoords[] = {
            -0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f,
    };
    // 顺时针顶点索引顺序
    static short index[] = {
            0, 1, 2, 0, 2, 3
    };
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private int mProgram;

    static float triangleCoords[] = {
            -0.5f, 0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f, 0.5f, 0.0f  // top right
    };

    private int mPositionHandle;
    private int mColorHandle;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    private int mMatrixHandler;

    //设置颜色，依次为红绿蓝和透明通道
    float color[] = {1.0f, 1.0f, 1.0f, 1.0f};

    public Square() {
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        fbb = bb.asFloatBuffer();
        fbb.put(squareCoords);
        fbb.position(0);
        // 初始化顶点索引顺序
        ByteBuffer bb1 = ByteBuffer.allocateDirect(squareCoords.length * 2);
        bb1.order(ByteOrder.nativeOrder());
        indexBuffer = bb1.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
    }

    public void onSurfaceChanged(int width, int height) {
        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    public void draw() {
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);


        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);


        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, fbb);


        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        //索引法绘制正方形
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
