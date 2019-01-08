package com.opengles.book.es2_0_test2.yuv;

import android.content.Context;
import android.util.AttributeSet;

import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;

/**
 * author:  ycl
 * date:  2019/1/8 11:01
 * desc:
 */
public class YuvSurfaceView extends EglSurfaceView {
    private YuvRender mYuvRender;

    public YuvSurfaceView(Context context) {
        this(context, null);
    }

    public YuvSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YuvSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mYuvRender = new YuvRender(context);
        setRenderer(mYuvRender);
        setRenderMode(EglSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


    public void setFrameData(int width, int height, byte[] by, byte[] bu, byte[] bv) {
        if (mYuvRender != null) {
            mYuvRender.setFrameData(width, height, by, bu, bv);
            requestRender();
        }
    }
}
