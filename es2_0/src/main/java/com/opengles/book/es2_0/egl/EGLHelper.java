package com.opengles.book.es2_0.egl;

import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-25 11:23
 * desc: EGL环境创建帮助类
 */
public class EGLHelper {
    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;
    private GL10 mGL;

    private int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    private int mEGLContextClientVersion = 2;// 默认是2.0版本

    // 自定义的surface支持的类型有三种
    private static final int SURFACE_PBUFFER = 1;
    private static final int SURFACE_PIM = 2;
    private static final int SURFACE_WINDOW = 3; // GLSurfaceView默认的创建的surface
    private int surfaceType = SURFACE_PBUFFER;
    private Object surface_native_obj;

    private int redSize, greenSize, blueSize, alphaSize, depthSize, renderType; // GlSurfaceView 里面是  EGL10.EGL_STENCIL_SIZE, stencilSize,;

    public EGLHelper(int width, int height) {
        redSize = 8;
        greenSize = 8;
        blueSize = 8;
        alphaSize = 8;
        depthSize = 16;
        renderType = 4;
        eglInit(width, height);
    }

    /**
     * 1. 取得EGL实例
     * 2. 选择Display
     * 3. 选择Config
     * 4. 创建Context
     * 5. 创建Surface
     * 6. 指定当前的环境为绘制环境
     * 7. 创建GL
     */
    public GLError eglInit(int width, int height) {

        //取得EGL实例
        mEgl = (EGL10) EGLContext.getEGL();

        //获取Display
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        //主版本号和副版本号
        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed");
        }

        //选择Config
        int[] configSpec = new int[]{
                EGL10.EGL_RED_SIZE, redSize,
                EGL10.EGL_GREEN_SIZE, greenSize,
                EGL10.EGL_BLUE_SIZE, blueSize,
                EGL10.EGL_ALPHA_SIZE, alphaSize,
                EGL10.EGL_DEPTH_SIZE, depthSize,//指定深度缓存(Z Buffer)大小
                EGL10.EGL_RENDERABLE_TYPE, renderType,//指定渲染api版本, EGL14.EGL_OPENGL_ES2_BIT
                EGL10.EGL_NONE};//总是以EGL10.EGL_NONE结尾
        int[] num_config = new int[1];
        if (!mEgl.eglChooseConfig(mEglDisplay, configSpec, null, 0, num_config)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        if (num_config[0] <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
        }
        EGLConfig[] configs = new EGLConfig[num_config[0]];
        if (!mEgl.eglChooseConfig(mEglDisplay, configSpec, configs, num_config[0],
                num_config)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }
        mEglConfig = configs[0];

        //创建Context
        int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, mEGLContextClientVersion, EGL10.EGL_NONE};
        mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig, EGL10.EGL_NO_CONTEXT,
                mEGLContextClientVersion != 0 ? attrib_list : null);
        if (mEglContext == null || mEglContext == EGL10.EGL_NO_CONTEXT) {
            mEglContext = null;
            throw new RuntimeException("createContext");
        }

        //创建Surface
        mEglSurface = createSurface(width, height);
        if (mEglSurface == null || mEglSurface == EGL10.EGL_NO_SURFACE) {
            int error = mEgl.eglGetError();
            if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
            }
            throw new RuntimeException("createEglSurface");
        }
        // 指定当前的环境为绘制环境
        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw new RuntimeException("EGLHelper eglMakeCurrent" + mEgl.eglGetError());
        }

        // 创建GL
        mGL = (GL10) mEglContext.getGL();
        return GLError.OK;
    }

    private EGLSurface createSurface(int width, int height) {
        int[] attrib_list = {
                EGL10.EGL_WIDTH, width,
                EGL10.EGL_HEIGHT, height,
                EGL10.EGL_NONE
        };
        switch (surfaceType) {
            case SURFACE_PBUFFER:
                return mEgl.eglCreatePbufferSurface(mEglDisplay, mEglConfig, attrib_list);
            case SURFACE_PIM:
                return mEgl.eglCreatePixmapSurface(mEglDisplay, mEglConfig, surface_native_obj, attrib_list);
            case SURFACE_WINDOW:
                return mEgl.eglCreateWindowSurface(mEglDisplay, mEglConfig, surface_native_obj, attrib_list);
        }
        return null;
    }

    public void destroy() {
        // destroy Surface
        mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
        mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
        // destroy context
        if (!mEgl.eglDestroyContext(mEglDisplay, mEglContext)) {
            Log.e("DefaultContextFactory", "display:" + mEglDisplay + " context: " + mEglContext);
            throw new RuntimeException("eglDestroyContex" + mEgl.eglGetError());
        }
        mEgl.eglTerminate(mEglDisplay);
    }


    // 定义创建的surface的类型
    public void setSurfaceType(int type, Object... obj) {
        this.surfaceType = type;
        if (obj != null) {
            this.surface_native_obj = obj[0];
        }
    }

    public GL10 getGL() {
        return mGL;
    }
}
