package com.ycl.es2_0_native.opengl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * author:  ycl
 * date:  2019/3/1 18:51
 * desc:
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private NativeOpengl mNativeOpengl;

    private OnSurfaceCreateListener onSurfaceCreateListener;

    public MySurfaceView(Context context) {
        this(context, null);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        getHolder().addCallback(this);

    }

    public MySurfaceView setNativeOpengl(NativeOpengl nativeOpengl) {
        mNativeOpengl = nativeOpengl;
        return this;
    }

    public MySurfaceView setSurfaceCreateListener(OnSurfaceCreateListener createListener) {
        this.onSurfaceCreateListener = createListener;
        return this;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mNativeOpengl != null) {
            mNativeOpengl.surfaceCreate(holder.getSurface());
        }

        if (onSurfaceCreateListener != null) {
            onSurfaceCreateListener.surfaceCreated();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mNativeOpengl != null) {
            mNativeOpengl.surfaceChange(width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mNativeOpengl != null) {
            mNativeOpengl.surfaceDestroy();
//            mNativeOpengl = null;
        }
    }

    public interface OnSurfaceCreateListener {
        void surfaceCreated();
    }

}
