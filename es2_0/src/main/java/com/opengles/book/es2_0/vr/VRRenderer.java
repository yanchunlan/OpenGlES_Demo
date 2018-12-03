package com.opengles.book.es2_0.vr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.opengles.book.es2_0.utils.BufferUtils;
import com.opengles.book.es2_0.utils.EasyGlUtils;
import com.opengles.book.es2_0.utils.ShaderUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-12-03 22:08
 * desc:
 */
public class VRRenderer implements GLSurfaceView.Renderer {
    private String vertexShaderCodes =
            "uniform mat4 projectMatrix;" +
                    "uniform mat4 viewMatrix;" +
                    "uniform mat4 mvpMatrix;" +
                    "uniform mat4 rotateMatrix;" +
                    "attribute vec3 vPosition;" +
                    "attribute vec2 vCoordinate;" +
                    "varying vec2 aCoordinate;" +
                    "void main(){" +
                    "gl_Position=projectMatrix*rotateMatrix*viewMatrix*mvpMatrix*vPosition" +// 注意此处是rotateMatrix 在view之前，目的是相机的变换
                    "}";
    private String fragmentShaderCodes = "precision highp float;" +
            "uniform sample2D uTexture;" +
            "varying vrc2 aCoordinate;" +
            "void main(){" +
            "gl_FragColor=texture2D(uTexture,aCoordinate);" +
            "}";
    private int mHProgram;
    private int mHProjMatrix;
    private int mHViewMatrix;
    private int mHModelMatrix;
    private int mHRotateMatrix;
    private int mHUTexture;
    private int mHPosition;
    private int mHCoordinate;

    private int textureId;


    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float[] mRotateMatrix = new float[16];

    private FloatBuffer posBuffer;
    private FloatBuffer cooBuffer;
    private int count;

    private Bitmap mBitmap;


    public VRRenderer(Context context) {
        try {
            mBitmap = BitmapFactory.decodeStream(context.getResources().getAssets().open("vr/360sp.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);

        mHProgram = ShaderUtils.createProgram("vr/skysphere.vert", "vr/skysphere.frag");
        mHProjMatrix = GLES20.glGetUniformLocation(mHProgram, "projectMatrix");
        mHViewMatrix = GLES20.glGetUniformLocation(mHProgram, "viewMatrix");
        mHModelMatrix = GLES20.glGetUniformLocation(mHProgram, "mvpMatrix");
        mHRotateMatrix = GLES20.glGetUniformLocation(mHProgram, "rotateMatrix");
        mHUTexture = GLES20.glGetUniformLocation(mHProgram, "uTexture");
        mHPosition = GLES20.glGetAttribLocation(mHProgram, "vPosition");
        mHCoordinate = GLES20.glGetAttribLocation(mHProgram, "aCoordinate");

        if (mBitmap != null && !mBitmap.isRecycled()) {
            textureId = EasyGlUtils.genTexturesWithParameter(1, 0, mBitmap)[0];
        } else {
            textureId = 0;
        }
        calculatingBall();
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        perspectiveM(mProjectMatrix, 0, 45, ratio, 1, 300);

        // 注意设置相机在球心，才能在球心观看角度
        Matrix.setLookAtM(mViewMatrix, 0,
                0, 0, 5,
                0, 0, -1,
                0, 1, 0);
        // 设置模型矩阵
        Matrix.setIdentityM(mMVPMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0, 0, 0, 0); // 每次绘制之前都须清除

        GLES20.glUseProgram(mHProgram);
        GLES20.glUniformMatrix4fv(mHProjMatrix, 1, false, mProjectMatrix, 0);
        GLES20.glUniformMatrix4fv(mHViewMatrix, 1, false, mViewMatrix, 0);
        GLES20.glUniformMatrix4fv(mHModelMatrix, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mHRotateMatrix, 1, false, mRotateMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); // 激活0通道
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition, 3, GLES20.GL_FLOAT, false, 0, posBuffer);
        GLES20.glEnableVertexAttribArray(mHCoordinate);
        GLES20.glVertexAttribPointer(mHCoordinate, 2, GLES20.GL_FLOAT, false, 0, cooBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, count); // 绘制线段

        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoordinate);
    }

    // 传感器传入的矩阵
    public void setMatrix(float[] matrix) {
        // copy一个数组
        System.arraycopy(matrix, 0, mRotateMatrix, 0, 16);
    }


    private void calculatingBall() {
        float radius = 2f;
        double angleSpan = Math.PI / 90f;
        ArrayList<Float> alVertix = new ArrayList<>();
        ArrayList<Float> textureVertix = new ArrayList<>(); // 纹理集合
        for (double angle = 0; angle < Math.PI; angle += angleSpan) {
            for (double hAngle = 0; hAngle < Math.PI * 2; hAngle += angleSpan) {
                float x0 = (float) (radius * Math.sin(angle) * Math.cos(hAngle));
                float y0 = (float) (radius * Math.sin(angle) * Math.sin(hAngle));
                float z0 = (float) (radius * Math.cos(angle));

                float x1 = (float) (radius * Math.sin(angle) * Math.cos(hAngle + angleSpan));
                float y1 = (float) (radius * Math.sin(angle) * Math.sin(hAngle + angleSpan));
                float z1 = (float) (radius * Math.cos(angle));

                float x2 = (float) (radius * Math.sin(angle + angleSpan) * Math.cos(hAngle + angleSpan));
                float y2 = (float) (radius * Math.sin(angle + angleSpan) * Math.sin(hAngle + angleSpan));
                float z2 = (float) (radius * Math.cos(angle + angleSpan));

                float x3 = (float) (radius * Math.sin(angle + angleSpan) * Math.cos(hAngle));
                float y3 = (float) (radius * Math.sin(angle + angleSpan) * Math.sin(hAngle));
                float z3 = (float) (radius * Math.cos(angle + angleSpan));

                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);
                alVertix.add(x0);
                alVertix.add(y0);
                alVertix.add(z0);
                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);

                // 因为纹理在0-1之间，所以此处hAngle应该是0-2pi，angle是0-pi,3维变st2维坐标
                float s0 = (float) (hAngle / Math.PI / 2);
                float s1 = (float) ((hAngle + angleSpan) / Math.PI / 2);
                float t0 = (float) (angle / Math.PI);
                float t1 = (float) ((angle + angleSpan) / Math.PI);


                // 不是很明白纹理原理
                // 103
                textureVertix.add(s1);// x1 y1对应纹理坐标
                textureVertix.add(t0);
                textureVertix.add(s0);// x0 y0对应纹理坐标
                textureVertix.add(t0);
                textureVertix.add(s0);// x3 y3对应纹理坐标
                textureVertix.add(t1);

                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);
                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);
                alVertix.add(x2);
                alVertix.add(y2);
                alVertix.add(z2);

                // 不是很明白纹理原理
                // 132
                textureVertix.add(s1);// x1 y1对应纹理坐标
                textureVertix.add(t0);
                textureVertix.add(s0);// x3 y3对应纹理坐标
                textureVertix.add(t1);
                textureVertix.add(s1);// x2 y3对应纹理坐标
                textureVertix.add(t1);
            }
        }
        count = alVertix.size() / 3;
        posBuffer = BufferUtils.list2FloatBuffer(alVertix);
        cooBuffer = BufferUtils.list2FloatBuffer(textureVertix);
    }


    private void perspectiveM(float[] m, int offset,
                              float fovy, float aspect, float zNear, float zFar) {

    }
}
