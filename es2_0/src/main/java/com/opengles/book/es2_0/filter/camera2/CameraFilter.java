package com.opengles.book.es2_0.filter.camera2;

import android.content.res.Resources;
import android.hardware.Camera;

import com.opengles.book.es2_0.filter.OesFilter;

/**
 * author:  ycl
 * date:  2018/11/30 17:24
 * desc:  控制相机前后镜头texture
 */
public class CameraFilter extends OesFilter {

    public CameraFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void initBuffer() {
        super.initBuffer();
        // 不断清除，并加入数据
        move();
    }


    // 前置后置决定纹理的方向
    @Override
    public void setFlag(int flag) {
        super.setFlag(flag);
        if (getFlag() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraFront();
        } else if (getFlag() == Camera.CameraInfo.CAMERA_FACING_BACK) {
            cameraBack();
        }
    }

    private void cameraFront() {
        float[] coord = new float[]{
                1.0f, 0.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
        };
        putBuffer(coord);
    }

    private void cameraBack() {
        float[] coord = new float[]{
                1.0f, 0.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
        };
        putBuffer(coord);
    }

    private void move() {
        float[] coord = new float[]{
                0f, 0f,
                0f, 1f,
                1f, 0f,
                1f, 1f
        };
        putBuffer(coord);
    }

    private void putBuffer(float[] coord) {
        mTexBuffer.clear();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }
}
