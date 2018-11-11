package com.opengles.book.es2_0.camera.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * author: ycl
 * date: 2018-11-10 15:56
 * desc: 对camera得封装
 */
public class KitkatCamera implements ICamera {
    private Config mConfig;
    private CameraSizeComparator mSizeComparator;
    private Camera mCamera;

    private Camera.Size picSize;
    private Camera.Size preSize;

    private Point mPicSize;
    private Point mPreSize;

    public KitkatCamera() {
        mConfig = new Config();
        mConfig.minPictureWidth = 720;
        mConfig.minPreviewWidth = 720;
        mConfig.rate = 1.778f;
        mSizeComparator = new CameraSizeComparator();
    }


    @Override
    public boolean open(int cameraId) {
        mCamera = Camera.open(cameraId);

        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            picSize = getPropSize(parameters.getSupportedPictureSizes(), mConfig.rate,
                    mConfig.minPictureWidth);
            preSize = getPropSize(parameters.getSupportedPreviewSizes(), mConfig.rate,
                    mConfig.minPreviewWidth);
            parameters.setPictureSize(picSize.width, picSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
            mCamera.setParameters(parameters);
            Camera.Size pic = parameters.getPictureSize();
            Camera.Size pre = parameters.getPreviewSize();
            mPicSize = new Point(pic.width, pic.height);
            mPreSize = new Point(pre.width, pre.height);
            return true;
        }
        return false;
    }


    @Override
    public void setConfig(Config config) {
        this.mConfig = config;
    }

    @Override
    public boolean preview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
        return false;
    }

    @Override
    public boolean switchTo(int cameraId) {
        close();
        open(cameraId);
        return false;
    }

    @Override
    public boolean close() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void setPreviewTexture(SurfaceTexture texture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Point getPreviewSize() {
        return mPreSize;
    }

    @Override
    public Point getPictureSize() {
        return mPicSize;
    }

    @Override
    public void setOnPreviewFrameCallback(final PreviewFrameCallback callback) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data, mPreSize.x, mPicSize.y);
                }
            });
        }
    }

    public void addBuffer(byte[] buffer) {
        if (mCamera != null) {
            mCamera.addCallbackBuffer(buffer);
        }
    }
    public void setOnPreviewFrameCallbackWithBuffer(final PreviewFrameCallback callback) {
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data, mPreSize.x, mPicSize.y);
                }
            });
        }
    }

    private Camera.Size getPropSize(List<Camera.Size> list,
                                    float rate, int minPictureWidth) {
        Collections.sort(list, mSizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            if (s.height >= minPictureWidth && equalRate(s, rate)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    private boolean equalRate(Camera.Size s, float rate) {
        float r = s.width / (float) s.height;
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    private class CameraSizeComparator implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            if (o1.height == o2.height) {
                return 0;
            } else if (o1.height > o2.height) {
                return 1;
            } else {
                return -1;
            }
        }
    }

}
