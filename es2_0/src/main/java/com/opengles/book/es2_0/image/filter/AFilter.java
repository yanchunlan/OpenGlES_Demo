package com.opengles.book.es2_0.image.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.opengles.book.es2_0.utils.BufferUtils;
import com.opengles.book.es2_0.utils.ShaderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-05 0:04
 * desc:
 */
public abstract class AFilter implements GLSurfaceView.Renderer {
    private Context context;
    private String vertexShaderCodes;
    private String fragmentShaderCodes;

    private final float[] vPos = {
            -1.0f, 1f,
            -1.0f, -1f,
            1.0f, -1f,
            1.0f, 1f,
    };
    private final float[] coords = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
    };

    private FloatBuffer posBuffer, coordsBuffer;

    private float[] viewMatrix = new float[16];
    private float[] projectMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private int program;
    private int glHMatrix;
    private int glHPosition;
    private int glHCoordinate;
    private int glHTexture;
    private int hIsHalf;
    private int glHUxy;

    private Bitmap bitmap;
    private int textureId;
    private boolean isHalf;
    private float uXY;

    public AFilter(Context context, String vertex, String fragment) {
        this.context = context;
        this.vertexShaderCodes = vertex;
        this.fragmentShaderCodes = fragment;
        posBuffer = BufferUtils.arr2FloatBuffer(vPos);
        coordsBuffer = BufferUtils.arr2FloatBuffer(coords);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1, 1, 1, 1);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        program = ShaderUtils.createProgram(context.getResources(), vertexShaderCodes, fragmentShaderCodes);

        glHPosition = GLES20.glGetAttribLocation(program, "vPosition");
        glHCoordinate = GLES20.glGetAttribLocation(program, "vCoordinate");
        glHMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        glHTexture = GLES20.glGetUniformLocation(program, "vTexture");

        hIsHalf = GLES20.glGetUniformLocation(program, "vIsHalf");
        glHUxy = GLES20.glGetUniformLocation(program, "uXY");
        onDrawCreatedSet(program);
    }

    protected abstract void onDrawSet();

    protected abstract void onDrawCreatedSet(int program);

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;

        if (width > height) {


        } else {


        }


        //设置相机位置
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mvpMatrix, 0, projectMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(program);
        onDrawSet();

    }


    private int createTexture() {
        if (bitmap != null && !bitmap.isRecycled()) {
            int[] texture = new int[1];
            GLES20.glGenTextures(1, texture, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);


            return texture[0];
        }
        return 0;
    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setHalf(boolean half) {
        isHalf = half;
    }
}
