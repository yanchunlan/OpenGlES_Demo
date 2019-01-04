package com.opengles.book.es2_0_test2.eglUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
public abstract class EglSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "EglSurfaceView";
    private Surface surface;
    private EGLContext eglContext;

    private EglThread mEglThread;
    private EglRenderer mRenderer;

    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;

    private int mRenderMode = RENDERMODE_CONTINUOUSLY;


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

    //  --------------------  callBack  start  --------------------------
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (surface == null) {
            surface = holder.getSurface();
        }
        mEglThread = new EglThread(new WeakReference<EglSurfaceView>(this));
        mEglThread.isCreate = true;
        mEglThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mEglThread.width = width;
        mEglThread.height = height;
        mEglThread.isChange = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mEglThread.onDestroy();
        mEglThread = null;
        mRenderer = null;
        surface = null;
        eglContext = null;
    }
    //  --------------------  callBack  end  --------------------------


    //  --------------------  other method  start  --------------------------

    public void setSurfaceAndEglContext(Surface surface, EGLContext eglContext) {
        this.surface = surface;
        this.eglContext = eglContext;
    }

    public EGLContext getEglContext() {
        return mEglThread != null ? mEglThread.getEglContext() : null;
    }

    public void requestRender() {
        if (mEglThread != null) mEglThread.requestRender();
    }

    public void setRenderer(EglRenderer renderer) {
        this.mRenderer = renderer;
    }

    public void setRenderMode(int renderMode) {
        this.mRenderMode = renderMode;
    }
    //  --------------------  other method  end  --------------------------


    public interface EglRenderer {
        void onSurfaceCreated();

        void onSurfaceChanged(int width, int height);

        void onDrawFrame();
    }

    static class EglThread extends Thread {
        private WeakReference<EglSurfaceView> eglSurfaceViewWeakReference = null;
        private EglHelper eglHelper = null;
        private Object object = null;

        private boolean isExit = false;
        private boolean isCreate = false;
        private boolean isChange = false;
        private boolean isStart = false;

        private int width;
        private int height;

        EglThread(WeakReference<EglSurfaceView> glSurfaceViewWeakRef) {
            this.eglSurfaceViewWeakReference = glSurfaceViewWeakRef;
        }

        @Override
        public void run() {
            super.run();

            isExit = false;
            isStart = false;
            object = new Object();
            eglHelper = new EglHelper();
            eglHelper.initEgl(eglSurfaceViewWeakReference.get().surface, eglSurfaceViewWeakReference.get().eglContext);

            while (true) {
                if (isExit) {
                    release(); //释放资源
                    break;
                }

                if (isStart) {
                    Log.d(TAG, "run: "+eglSurfaceViewWeakReference.get().mRenderMode);
                    if (eglSurfaceViewWeakReference.get().mRenderMode == RENDERMODE_WHEN_DIRTY) {
                        // 一次就堵塞
                        synchronized (object) {
                            try {
                                object.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    } else if (eglSurfaceViewWeakReference.get().mRenderMode == RENDERMODE_CONTINUOUSLY) {
                        try {
                            Thread.sleep(1000 / 60);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        throw new RuntimeException("mRenderMode is wrong value");
                    }
                }

                onCreate();
                onChange(width, height);
                onDraw();

                isStart = true;
            }
        }

        private void release() {
            if (eglHelper != null) {
                eglHelper.destroyEgl();
                eglHelper = null;
                object = null;
                eglSurfaceViewWeakReference = null;
            }
        }

        public void onCreate() {
            if (isCreate && eglSurfaceViewWeakReference.get().mRenderer != null) {
                isCreate = false;
                eglSurfaceViewWeakReference.get().mRenderer.onSurfaceCreated();
            }
        }

        public void onChange(int width, int height) {
            if (isChange && eglSurfaceViewWeakReference.get().mRenderer != null) {
                isChange = false;
                eglSurfaceViewWeakReference.get().mRenderer.onSurfaceChanged(width, height);
            }
        }

        public void onDraw() {
            if (eglSurfaceViewWeakReference.get().mRenderer != null && eglHelper != null) {
                eglSurfaceViewWeakReference.get().mRenderer.onDrawFrame();

                // 执行一次无效果，不知道为何，此处多执行一次
                if(!isStart)
                {
                    eglSurfaceViewWeakReference.get().mRenderer.onDrawFrame();
                }
                eglHelper.swapBuffers();
            }
        }

        public void onDestroy() {
            isExit = true;
            requestRender();
        }

        private void requestRender() {
            if (object != null) {
                synchronized (object) {
                    object.notifyAll();
                }
            }
        }

        public EGLContext getEglContext() {
            return eglHelper != null ? eglHelper.getEglContext() : null;
        }
    }
}
