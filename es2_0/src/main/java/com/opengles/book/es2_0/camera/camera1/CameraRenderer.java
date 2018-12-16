package com.opengles.book.es2_0.camera.camera1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

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

    private Context mContext;

    public CameraRenderer(Resources res) {
        mOesFilter = new OesFilter(res);
    }

    public void setContext(Context context) {
        mContext = context;
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
        Log.d("123", "onSurfaceChanged: ");
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
//        calculateMatrix(mContext);
    }

    private void calculateMatrix(Context context) {
        // 重置matrix
        Matrix.setIdentityM(matrix, 0);

        int angle = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (angle) {
            case Surface.ROTATION_0:
                Log.d("123", "0");
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Matrix.rotateM(matrix,0,90, 0, 0, 1);
                    Matrix.rotateM(matrix,0,180, 1, 0, 0);
                } else {
                    Matrix.rotateM(matrix,0,90f, 0f, 0f, 1f);
                }

                break;
            case Surface.ROTATION_90:
                Log.d("123", "90");
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Matrix.rotateM(matrix,0,180, 0, 0, 1);
                    Matrix.rotateM(matrix,0,180, 0, 1, 0);
                } else {
                    Matrix.rotateM(matrix,0,90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_180:
                Log.d("123", "180");
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Matrix.rotateM(matrix,0,90f, 0.0f, 0f, 1f);
                    Matrix.rotateM(matrix,0,180f, 0.0f, 1f, 0f);
                } else {
                    Matrix.rotateM(matrix,0,-90, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_270:
                Log.d("123", "270");
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Matrix.rotateM(matrix,0,180f, 0.0f, 1f, 0f);
                } else {
                    Matrix.rotateM(matrix,0,0f, 0f, 0f, 1f);
                }
                break;
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
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return texture[0];
    }
}

