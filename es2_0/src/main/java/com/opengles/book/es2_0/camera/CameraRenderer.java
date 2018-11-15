package com.opengles.book.es2_0.camera;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.opengles.book.es2_0.filter.AFilter;
import com.opengles.book.es2_0.filter.OesFilter;
import com.opengles.book.es2_0.utils.MatrixUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-10 15:37
 * desc:
 */
public class CameraRenderer implements GLSurfaceView.Renderer {

    private float[] matrix = new float[16];
    private SurfaceTexture surfaceTexture;
    private int width, height;
    private int dataWidth, dataHeight;
    private AFilter mOesFilter;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    public CameraRenderer(Resources res) {
        mOesFilter = new OesFilter(res);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int texture = createTextureId();
        surfaceTexture = new SurfaceTexture(texture);
        mOesFilter.create();
        mOesFilter.setTextureId(texture);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        setViewSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (surfaceTexture != null) {
            surfaceTexture.updateTexImage();
        }
        mOesFilter.draw();
    }

    public void setCameraId(int id) {
        this.cameraId = id;
        calculateMatrix();
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public void setDataSize(int w, int h) {
        this.dataWidth = w;
        this.dataHeight = h;
        calculateMatrix();
    }

    public void setViewSize(int w, int h) {
        this.width = w;
        this.height = h;
        calculateMatrix();
    }

    private void calculateMatrix() {
        MatrixUtils.getShowMatrix(matrix, this.dataWidth, this.dataHeight,
                this.width, this.height);
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            MatrixUtils.flip(matrix, true, false);
            MatrixUtils.rotate(matrix, 90);
        } else {
            MatrixUtils.rotate(matrix, 270);
        }
        mOesFilter.setMatrix(matrix);
    }

    private int createTextureId() {
        int[] texture = new int[1];
        // 获取texture是不需要设置类型的
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST); // GL_NEAREST
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        return texture[0];
    }
}

