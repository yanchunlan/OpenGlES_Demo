package com.opengles.book.es2_0_test2.utils;

import android.util.Log;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * author: ycl
 * date: 2018-12-24 23:16
 * desc:
 */
public class EglHelper {
    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;

    private int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    private int mEGLContextClientVersion = 2;// 默认是2.0版本


    public void initEgl(Surface surface, EGLContext eglContext ) {
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
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 8,//指定深度缓存(Z Buffer)大小
                EGL10.EGL_STENCIL_SIZE, 8,//指定深度缓存(Z Buffer)大小
                EGL10.EGL_RENDERABLE_TYPE, 4,//指定渲染api版本, EGL14.EGL_OPENGL_ES2_BIT,默认 2.0 就是4
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

        //创建Context
        int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, mEGLContextClientVersion, EGL10.EGL_NONE};
        if (eglContext != null) {
            mEglContext = mEgl.eglCreateContext(mEglDisplay, configs[0], eglContext, attrib_list);
        } else {
            mEglContext = mEgl.eglCreateContext(mEglDisplay, configs[0], EGL10.EGL_NO_CONTEXT, attrib_list);
        }
        if (mEglContext == null || mEglContext == EGL10.EGL_NO_CONTEXT) {
            mEglContext = null;
            throw new RuntimeException("createContext");
        }

        //创建Surface
        if (surface == null) {
            mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, configs[0], surface, null);
        } else {
            mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, configs[0], surface, null);
        }
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
    }

    public boolean swapBuffers() {
        if (mEgl != null) {
            return mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
        } else {
            throw new RuntimeException("egl is null");
        }
    }

    public void destroyEgl() {
        if (mEgl != null) {
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

            mEglSurface = null;
            mEglContext = null;
            mEglDisplay = null;
            mEgl = null;
        }
    }

    public EGLContext getEglContext() {
        return mEglContext;
    }
}
