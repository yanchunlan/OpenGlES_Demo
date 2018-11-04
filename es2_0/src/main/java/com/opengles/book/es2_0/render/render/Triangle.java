package com.opengles.book.es2_0.render.render;

import android.opengl.GLES20;
import android.view.View;

import com.opengles.book.es2_0.base.BaseRenderer;
import com.opengles.book.es2_0.utils.BufferUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-10-28 15:57
 * desc: 直角三角形
 */
public class Triangle extends BaseRenderer {


    // 顶点着色器  gl_Position是shader的内置变量 -> 顶点位置
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main(){" +
                    "gl_Position=vPosition;" +
                    "}";
    // 片元着色器 gl_FragColor是shader的内置变量 -> 片元颜色
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    "gl_FragColor=vColor;" +
                    "}";

    private FloatBuffer vertexBuffer;
    private float triangleCoords[] = {
            0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f
    };
    private float color[] = {1f, 1f, 1f, 1f};

    private int mProgram;

    private int mPositionHandle;
    private int mColorHandle;

    private int COORDS_PER_VERTEX = 3;// 顶点3
    // 顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    // 顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4;// 每个顶点4字节



    public Triangle(@NotNull View view) {
        super(view);
        init();
    }

    private void init() {
        // 申请底层空间
        vertexBuffer = BufferUtils.arr2FloatBuffer(triangleCoords);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);


        // 创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        // 将顶点着色器加入程序
        GLES20.glAttachShader(mProgram, vertexShader);
        // 将片元着色器加入程序
        GLES20.glAttachShader(mProgram, fragmentShader);
        // 连接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

        // 将程序加入到OpenGlES2.0环境
        GLES20.glUseProgram(mProgram);

        // 获取顶点着色器的vPosition句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // 准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle,// 句柄
                COORDS_PER_VERTEX, // 几个顶点
                GLES20.GL_FLOAT, // 单位
                false, // 是否标准化
                vertexStride, //  跨度，一般情况下写0系统会自动识别。识别方式为size*sizeof(数组定义时报类型)
                vertexBuffer // 顶点数据
                );


        // 获取片元着色器vColor句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // 设置三角形颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);


        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        // 禁止顶点数组的句柄(用完之后禁止掉)
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
}
