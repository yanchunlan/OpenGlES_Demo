package com.opengles.book.es2_0_test2.encodec.render;

import android.content.Context;

import com.opengles.book.es2_0_test2.camera.render.CameraTextureRender;
import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;

/**
 * author:  ycl
 * date:  2019/1/7 14:57
 * desc:
 */
public class EncodecRender implements EglSurfaceView.EglRenderer {

    private CameraTextureRender mTextureRender;

    private int textureId = -1;

    public EncodecRender(Context context, int textureId) {
        this.textureId = textureId;
        mTextureRender = new CameraTextureRender(context);
    }

    @Override
    public void onSurfaceCreated() {
        mTextureRender.onCreate();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        mTextureRender.onChange(width, height);
    }

    @Override
    public void onDrawFrame() {
        if (textureId != -1) {
            mTextureRender.onDraw(textureId);
        }
    }
}
