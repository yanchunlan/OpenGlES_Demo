package com.opengles.book.es2_0.camera.camera2;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.opengles.book.es2_0.camera.camera2.camera_render.GLRenderer;
import com.opengles.book.es2_0.filter.camera2.ZipPkmAnimationFilter;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-26 22:39
 * desc:
 */
public class Camera2Renderer implements GLSurfaceView.Renderer {
    private static final String TAG = "Camera2Renderer";
    private Context mContext;

    private GLRenderer mRenderer;
    private Camera2Renderer.CallBack mCallBack;
    private int frameCallBackWidth, frameCallBackHeight; // 回调数据的宽高


    private Object mSurface;
    private GLView mGLView;


    private Point mDataSize; // 数据的大小
    private Point mWindowSize; // 输出界面的大小


    public Camera2Renderer(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        mGLView = new GLView(mContext);

        //需要有父类才能attach,避免GLView的attachToWindow和detachFromWindow崩溃
        ViewGroup group = new ViewGroup(mContext) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };
        group.addView(mGLView);
        group.setVisibility(View.GONE);



        // 设置默认宽高
        mDataSize = new Point(720,1280);
        mWindowSize = new Point(720,1280);
    }

    //  ------------  glSurfaceView回调  start ---------------
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
    //  ------------  glSurfaceView回调  end ---------------


    //   ------------  callback回调  start ---------------
    public void surfaceCreated(SurfaceHolder holder) {
        mSurface = holder;
        mGLView.surfaceCreated(holder);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mWindowSize.x = width;
        this.mWindowSize.y = height;
        mGLView.surfaceChanged(holder, format, width, height);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mGLView.surfaceDestroyed(holder);
    }

    //   ------------  callback回调  end ---------------

    public void addFilter(ZipPkmAnimationFilter filter) {

    }

    public void takePhoto() {

    }

    public void onResume() {

    }

    public void onPause() {
    }

    public void destroy() {
    }


    // 相机宽高 其实就是数据，图片的宽高，矩阵变换需要
    public void setDataSize(int width, int height) {
        mDataSize.x = width;
        mDataSize.y = height;
    }

    // 相机方向 -- 因为需要根据相机方向做相应的矩阵变换
    public void setImageDirection(int cameraId) {

    }

    // 相机预览的texture
    public SurfaceTexture getTexture() {
        return null;
    }

    // 相机获取数据之后不断调用刷新 requestRender
    public void requestRender() {

    }


    //  callback
    public void setCallBackSize(int w, int h) {
        this.frameCallBackWidth = w;
        this.frameCallBackHeight = h;
        Log.i(TAG, "setCallBackSize:  ->    w: " + w + " h: " + h);
    }

    public void setCallBack(Camera2Renderer.CallBack callBack) {
        mCallBack = callBack;
    }

    public void setRenderer(GLRenderer renderer) {
        mRenderer = renderer;
    }


    interface CallBack {
        public void onFrame(ByteBuffer buffer, int w, int h);
    }


    private class GLView extends GLSurfaceView {
        public GLView(Context context) {
            this(context, null);
        }

        public GLView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            getHolder().addCallback(null);

            // 下面重新创建EGL的window是外部提供的surface
            setEGLWindowSurfaceFactory(new EGLWindowSurfaceFactory() {
                @Override
                public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
                    return egl.eglCreateWindowSurface(display, config, mSurface, null);
                }

                @Override
                public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {

                }
            });
            setEGLContextClientVersion(2);

            setRenderer(Camera2Renderer.this);
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            setPreserveEGLContextOnPause(true);// 暂停的时候保留EGLContext
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }

        public void clear() {

        }
    }
}

