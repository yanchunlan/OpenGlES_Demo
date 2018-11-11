package com.opengles.book.es2_0.camera.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;

/**
 * author: ycl
 * date: 2018-11-10 15:49
 * desc:
 */
public interface ICamera {
    boolean open(int cameraId);

    void setConfig(Config config);

    boolean preview();

    boolean switchTo(int cameraId);

    boolean close();

    void setPreviewTexture(SurfaceTexture texture);

    Point getPreviewSize();

    Point getPictureSize();

    void setOnPreviewFrameCallback(PreviewFrameCallback callback);
    void setOnPreviewFrameCallbackWithBuffer(PreviewFrameCallback callback);

    class Config {
        float rate; // 宽高比
        int minPreviewWidth;
        int minPictureWidth;
    }

    interface PreviewFrameCallback {
        void onPreviewFrame(byte[] bytes, int width, int height);
    }
}


