package com.opengles.book.es2_0.camera.camera2.camera_render;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.opengles.book.es2_0.camera.camera2.Camera2Renderer;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-26 23:43
 * desc:
 */
public class Camera1Renderer implements GLRenderer {

    private Camera2Renderer mController;
    private Camera mCamera;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    public Camera1Renderer(Camera2Renderer controller) {
        this.mController = controller;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        surfaceDestroyed(null);

        mCamera = Camera.open(cameraId);

        mController.setImageDirection(cameraId);
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        mController.setDataSize(size.width, size.height);

        // 设置相机预览界面
        try {
            mCamera.setPreviewTexture(mController.getTexture());
            mController.getTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    mController.requestRender();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
