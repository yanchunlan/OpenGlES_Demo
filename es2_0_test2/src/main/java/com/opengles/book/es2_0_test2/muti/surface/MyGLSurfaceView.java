package com.opengles.book.es2_0_test2.muti.surface;

import android.content.Context;
import android.util.AttributeSet;

import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;

/**
 * author: ycl
 * date: 2019-01-04 10:56
 * desc:
 */
public class MyGLSurfaceView extends EglSurfaceView {
    private MyRender mMyRender;

    public MyGLSurfaceView(Context context) {
        this(context, null);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMyRender = new MyRender(context);
        setRenderer(mMyRender);
        setRenderMode(EglSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void setOnRenderCreateListener(MyRender.OnRenderCreateListener onRenderCreateListener) {
        if (mMyRender != null) {
            mMyRender.setOnRenderCreateListener(onRenderCreateListener);
        }
    }
}
