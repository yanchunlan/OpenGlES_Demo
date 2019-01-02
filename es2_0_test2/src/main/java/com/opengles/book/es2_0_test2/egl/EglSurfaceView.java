package com.opengles.book.es2_0_test2.egl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLContext;

/**
 * author: ycl
 * date: 2018-12-24 23:28
 * desc:
 */
public class EglSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private Surface surface;
    private EGLContext eglContext;

    private EglThread mEglThread;
    private EglRenderer mRenderer;

    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;


    public EglSurfaceView(Context context) {
        this(context, null);
    }

    public EglSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EglSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (surface == null) {
            surface = holder.getSurface();
        }
        mEglThread = new EglThread(new WeakReference<EglSurfaceView>(this));
        mEglThread.start();
        mEglThread.surfaceCreated();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mEglThread.surfaceChanged(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mEglThread.surfaceDestroyed();
        mEglThread = null;
        mRenderer = null;
        surface = null;
        eglContext = null;
    }

    public void setSurfaceAndEglContext(Surface surface, EGLContext eglContext) {
        this.surface = surface;
        this.eglContext = eglContext;
    }

    public void setRenderer(EglRenderer renderer) {
        mRenderer = renderer;
    }

    public void setRenderMode(int renderMode) {
        if (mEglThread != null) {
            mEglThread.setRenderMode(renderMode);
        }
    }


    public interface EglRenderer {
        void onSurfaceCreated();

        void onSurfaceChanged(int width, int height);

        void onDrawFrame();
    }

    static class EglThread extends Thread {
        private WeakReference<EglSurfaceView> eglSurfaceViewWeakReference;
        private EglHelper eglHelper;
        private Object object = null;

        private boolean isExit = false;
        private boolean isCreate = false;
        private boolean isChange = false;
        private boolean isStart = false;

        private int width;
        private int height;

        private int mRenderMode = RENDERMODE_CONTINUOUSLY;

        EglThread(WeakReference<EglSurfaceView> glSurfaceViewWeakRef) {
            this.eglSurfaceViewWeakReference = glSurfaceViewWeakRef;
        }

        public void setRenderMode(int renderMode) {
            mRenderMode = renderMode;
        }

        public void surfaceCreated() {

        }

        public void surfaceChanged(int width, int height) {

        }

        public void surfaceDestroyed() {

        }

        public EGLContext getEglContext() {
            return eglHelper != null ? eglHelper.getEglContext() : null;
        }
    }
}
