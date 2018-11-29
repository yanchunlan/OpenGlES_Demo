package com.opengles.book.es2_0.etc;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-12 22:18
 * desc:
 */
public class ZipGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private ZipRenderer mZipRenderer;

    public ZipGlSurfaceView(Context context) {
        this(context, null);
    }

    public ZipGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);

        setZOrderOnTop(true); // 设置在z轴上层
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT); // 系统选择支持半透明的格式（许多alpha位)
        setEGLConfigChooser(8, 8, 8, 8,
                16, 0); //设置GL的颜色，深度


        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mZipRenderer = new ZipRenderer(this.getContext());
    }

    public void setScaleType(int type) {
        checkNotNull(mZipRenderer, "mZipRenderer is null");
        mZipRenderer.setScaleType(ZipRenderer.TYPE, type);
    }

    public void setAnimation(String path, int timeStep) {
        checkNotNull(mZipRenderer, "mZipRenderer is null");
        mZipRenderer.setAnimation(this, path, timeStep);
    }

    public void start() {
        checkNotNull(mZipRenderer, "mZipRenderer is null");
        mZipRenderer.start();
    }

    public void stop() {
        checkNotNull(mZipRenderer, "mZipRenderer is null");
        mZipRenderer.stop();
    }

    public boolean isPlay() {
        checkNotNull(mZipRenderer, "mZipRenderer is null");
        return mZipRenderer.isPlay();
    }

    public void setChangeListener(StateChangeListener changeListener) {
        checkNotNull(mZipRenderer, "mZipRenderer is null");
        mZipRenderer.setChangeListener(changeListener);
    }


    // 此类的render是一个包装类
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        checkNotNull(mZipRenderer, "mZipRenderer is null");
        mZipRenderer.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        checkNotNull(mZipRenderer, "mZipRenderer is null");
        mZipRenderer.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        checkNotNull(mZipRenderer, "mZipRenderer is null");
        mZipRenderer.onDrawFrame(gl);
    }


    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        } else {
            return reference;
        }
    }
}
