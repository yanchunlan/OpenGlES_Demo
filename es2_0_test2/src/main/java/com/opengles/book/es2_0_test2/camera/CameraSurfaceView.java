package com.opengles.book.es2_0_test2.camera;

import android.content.Context;
import android.util.AttributeSet;

import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;

/**
 * author:  ycl
 * date:  2019/1/4 17:10
 * desc:
 */
public class CameraSurfaceView extends EglSurfaceView {
    private Camera mCamera;

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void onDestroy() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    public void previewAngle(Context ctx) {

    }
}
