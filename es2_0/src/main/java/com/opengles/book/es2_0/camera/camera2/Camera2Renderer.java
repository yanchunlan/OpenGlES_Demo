package com.opengles.book.es2_0.camera.camera2;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.opengles.book.es2_0.camera.CameraConfig;
import com.opengles.book.es2_0.camera.camera2.camera_render.GLRenderer;
import com.opengles.book.es2_0.filter.AFilter;
import com.opengles.book.es2_0.filter.camera2.GroupFilter;
import com.opengles.book.es2_0.filter.camera2.NoFilter;
import com.opengles.book.es2_0.filter.camera2.TextureFilter;
import com.opengles.book.es2_0.utils.EasyGlUtils;
import com.opengles.book.es2_0.utils.MatrixUtils;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private SurfaceHolder mSurface;
    private GLView mGLView;

    private GLRenderer mRenderer;                               //用户附加的Renderer或用来监听Renderer
    private TextureFilter mEffectFilter;                        //特效处理的Filter
    private GroupFilter mGroupFilter;                           //中间特效
    private AFilter mShowFilter;                                //用来渲染输出的Filter

    private Point mDataSize;                                    // 数据的大小
    private Point mWindowSize;                                  // 输出界面的大小

    private int mDirectionFlag = -1;                             //AiyaFilter方向flag
    private AtomicBoolean isParamSet = new AtomicBoolean(false);
    private float[] SM = new float[16];
    private int mShowType = MatrixUtils.TYPE_CENTERCROP;         // 输出到屏幕上的方式

    private float[] callBackOM = new float[16];                  // 用于绘制回调缩放的矩阵

    // 创建离屏buffer，用于最后导出数据
    private int[] mExportFrame = new int[1];
    private int[] mExportTexture = new int[1];

    private boolean isShoot = false;                              // 一次拍摄flag
    private ByteBuffer[] outPutBuffer = new ByteBuffer[3];         // 用于存储回调数据的buffer
    private int indexOutput = 0;                                   //回调数据使用的buffer索引


    private Camera2Renderer.CallBack mCallBack;
    private int frameCallBackWidth, frameCallBackHeight;        // 回调数据的宽高


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

        mEffectFilter = new TextureFilter(mContext.getResources());
        mShowFilter = new NoFilter(mContext.getResources());
        mGroupFilter = new GroupFilter(mContext.getResources());

        // 设置默认宽高
        mDataSize = new Point(CameraConfig.width, CameraConfig.height);
        mWindowSize = new Point(CameraConfig.width, CameraConfig.height);
    }

    //  ------------  glSurfaceView回调  start ---------------
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mEffectFilter.create();
        mGroupFilter.create();
        mShowFilter.create();
        if (!isParamSet.get()) {
            if (mRenderer != null) {
                mRenderer.onSurfaceCreated(gl, config);
            }
            sdkParamSet();
        }

        calculateCallBackOM();
        mEffectFilter.setFlag(mDirectionFlag);

        deleteFrameBuffer();
        GLES20.glGenFramebuffers(1, mExportFrame, 0);
        EasyGlUtils.genTexturesWithParameter(1, mExportTexture,
                0, GLES20.GL_RGBA,
                mDataSize.x, mDataSize.y);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        MatrixUtils.getMatrix(SM, mShowType, mDataSize.x, mDataSize.y, width, height);
        mShowFilter.setSize(width, height);
        mShowFilter.setMatrix(SM);

        mGroupFilter.setSize(mDataSize.x, mDataSize.y);
        mEffectFilter.setSize(mDataSize.x, mDataSize.y);
        mShowFilter.setSize(mDataSize.x, mDataSize.y);
        if (mRenderer != null) {
            mRenderer.onSurfaceChanged(gl, width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (isParamSet.get()) {
            mEffectFilter.draw();

            mGroupFilter.setTextureId(mEffectFilter.getfTexture());// frameBuffer
            mGroupFilter.draw();

            // 显示在屏幕上的界面的宽高
            GLES20.glViewport(0, 0, mWindowSize.x, mWindowSize.y);

            mShowFilter.setMatrix(SM);
            mShowFilter.setTextureId(mGroupFilter.getOutputTexture()); // 可能是离开屏幕的texture，也可以能是缓存的textureId
            mShowFilter.draw();

            if (mRenderer != null) {
                mRenderer.onDrawFrame(gl);
            }
            callBackIfNeed();
        }
    }

    //需要回调，则缩放图片到指定大小，读取数据并回调
    private void callBackIfNeed() {
        if (mCallBack != null && isShoot) {
            // outPutBuffer 只缓存3个，所以需要
            indexOutput = indexOutput++ >= 2 ? 0 : indexOutput;
            if (outPutBuffer[indexOutput] == null) {
                outPutBuffer[indexOutput] = ByteBuffer.allocate(frameCallBackWidth *
                        frameCallBackHeight * 4);
            }
            GLES20.glViewport(0, 0, frameCallBackWidth, frameCallBackHeight);
            EasyGlUtils.bindFrameTexture(mExportFrame[0], mExportTexture[0]);

            mShowFilter.setMatrix(callBackOM);
            mShowFilter.draw();

            // 读取数据回调
            GLES20.glReadPixels(0, 0, frameCallBackWidth, frameCallBackHeight,
                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, outPutBuffer[indexOutput]);
            mCallBack.onFrame(outPutBuffer[indexOutput].array(), frameCallBackWidth, frameCallBackHeight);


            isShoot = false;
            EasyGlUtils.unBindFrameBuffer();
            mShowFilter.setMatrix(SM);
        }
    }

    private void calculateCallBackOM() {
        if (frameCallBackHeight > 0 && frameCallBackWidth > 0 && mDataSize.x > 0 && mDataSize.y > 0) {
            // 计算输出的变换矩阵
            MatrixUtils.getMatrix(callBackOM, MatrixUtils.TYPE_CENTERCROP,
                    mDataSize.x, mDataSize.y,
                    frameCallBackWidth, frameCallBackHeight);
            MatrixUtils.flip(callBackOM, false, true);
        }
    }

    private void sdkParamSet() {
        if (!isParamSet.get() && mDataSize.x > 0 && mDataSize.y > 0) {
            isParamSet.set(true);
        }
    }


    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, mExportFrame, 0);
        GLES20.glDeleteTextures(1, mExportTexture, 0);
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


    public void addFilter(AFilter filter) {
        mGroupFilter.addFilter(filter);
    }

    public void takePhoto() {
        this.isShoot = true;
    }

    public void onResume() {
        mGLView.onResume();
    }

    public void onPause() {
        mGLView.onPause();
    }

    public void create(int width, int height) {
        mGLView.onAttachedToWindow();
        surfaceCreated(mSurface);
        surfaceChanged(null, 0, width, height);
    }

    public void destroy() {
        if (mRenderer != null) {
            mRenderer.surfaceDestroyed(null);
        }

        mGLView.surfaceDestroyed(null);
        mGLView.onDetachedFromWindow();
        mGLView.clear();
    }


    // 相机宽高 其实就是数据，图片的宽高，矩阵变换需要
    public void setDataSize(int width, int height) {
        mDataSize.x = width;
        mDataSize.y = height;
    }

    // 相机方向 -- 因为需要根据相机方向做相应的矩阵变换
    public void setImageDirection(int cameraId) {
        this.mDirectionFlag = cameraId;
    }

    // 相机预览的texture
    public SurfaceTexture getTexture() {
        return mEffectFilter.getTexture();
    }

    // 相机获取数据之后不断调用刷新 requestRender
    public void requestRender() {
        mGLView.requestRender();
    }


    //  callback
    public void setCallBackSize(int w, int h) {
        this.frameCallBackWidth = w;
        this.frameCallBackHeight = h;
        Log.i(TAG, "setCallBackSize:  ->    w: " + w + " h: " + h);
    }

    public void setCallBack(Camera2Renderer.CallBack callBack) {
        if (frameCallBackWidth > 0 && frameCallBackHeight > 0) {
            if (outPutBuffer != null) {
                outPutBuffer = new ByteBuffer[3];
            }
            calculateCallBackOM();
            mCallBack = callBack;
        } else {
            mCallBack = null;
        }
    }

    public void setRenderer(GLRenderer renderer) {
        mRenderer = renderer;
    }


    interface CallBack {
        public void onFrame(byte[] buffer, int w, int h);
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

