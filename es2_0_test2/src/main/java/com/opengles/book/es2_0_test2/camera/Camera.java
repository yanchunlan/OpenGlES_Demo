package com.opengles.book.es2_0_test2.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;

import com.opengles.book.es2_0_test2.utils.DisplayUtil;

import java.io.IOException;
import java.util.List;

/**
 * author:  ycl
 * date:  2019/1/4 17:11
 * desc:
 */
public class Camera {
    private android.hardware.Camera camera;
    private SurfaceTexture surfaceTexture;

    private int width;
    private int height;

    public Camera(Context context) {
        this.width = DisplayUtil.getScreenWidth(context);
        this.height = DisplayUtil.getScreenHeight(context);
    }

    public void initCamera(SurfaceTexture surfaceTexture, int cameraId) {
        this.surfaceTexture = surfaceTexture;
        setCameraParm(cameraId);
    }

    private void setCameraParm(int cameraId) {
        try {
            camera = android.hardware.Camera.open(cameraId);
            camera.setPreviewTexture(surfaceTexture);
            android.hardware.Camera.Parameters parameters = camera.getParameters();

            parameters.setFlashMode("off");
            parameters.setPreviewFormat(ImageFormat.NV21);

            android.hardware.Camera.Size size = getFitSize(parameters.getSupportedPictureSizes());
            parameters.setPictureSize(size.width, size.height);

            size = getFitSize(parameters.getSupportedPreviewSizes());
            parameters.setPreviewSize(size.width, size.height);

            camera.setParameters(parameters);
            camera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPreview() {
        if (camera != null) {
            camera.startPreview();
            camera.release();
            camera = null;
        }
    }

    public void changeCamera(int cameraId) {
        if (camera != null) {
            stopPreview();
        }
        setCameraParm(cameraId);
    }

    // 保持宽高比与屏幕宽高比一致
    private android.hardware.Camera.Size getFitSize(List<android.hardware.Camera.Size> sizes) {
        if (width < height) {
            int t = height;
            height = width;
            width = t;
        }

        for (android.hardware.Camera.Size size : sizes) {
            if (1.0f * size.width / size.height == 1.0f * width / height) {
                return size;
            }
        }
        return sizes.get(0);
    }


}
