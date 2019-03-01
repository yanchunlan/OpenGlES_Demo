//
// Created by pc on 2019/2/27.
//

#include "EGLHelper.h"


EGLHelper::EGLHelper() {
    mEglDisplay = EGL_NO_DISPLAY;
    mEglSurface = EGL_NO_SURFACE;
    mEglContext = EGL_NO_CONTEXT;
    mEglConfig = NULL;
}

EGLHelper::~EGLHelper() {


}

int EGLHelper::initEgl(EGLNativeWindowType win) {
    // 1.
    mEglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (mEglDisplay == EGL_NO_DISPLAY) {
        LOGE("eglGetDisplay error");
        return -1;
    }
    // 2.
    EGLint *version = new EGLint[2];
    if (!eglInitialize(mEglDisplay, &version[0], &version[1])) {
        LOGE("eglInitialize error");
        return -1;
    }

    // 3.
    const EGLint attribs[] = {
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_ALPHA_SIZE, 8,
            EGL_DEPTH_SIZE, 8,// 深度
            EGL_STENCIL_SIZE, 8, // 模板
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, // 4 渲染api版本
            EGL_NONE
    };
    EGLint num_config;
    if (!eglChooseConfig(mEglDisplay, attribs, NULL, 1, &num_config)) {
        LOGE("eglChooseConfig  error 1");
        return -1;
    }

    // 4.
    if (!eglChooseConfig(mEglDisplay, attribs, &mEglConfig, num_config, &num_config)) {
        LOGE("eglChooseConfig  error 2");
        return -1;
    }

    // 5.
    int attrib_list[] = {EGL_CONTEXT_CLIENT_VERSION, 2,
                         EGL_NONE};
    mEglContext = eglCreateContext(mEglDisplay, mEglConfig, EGL_NO_CONTEXT, attrib_list);
    if (mEglContext == EGL_NO_CONTEXT) {
        LOGE("eglCreateContext  error");
        return -1;
    }

    // 6.
    mEglSurface = eglCreateWindowSurface(mEglDisplay, mEglConfig, win, NULL);
    if (mEglSurface == EGL_NO_SURFACE) {
        LOGE("eglCreateWindowSurface  error");
        return -1;
    }

    // 7.
    if (eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
        LOGE("eglMakeCurrent  error");
        return -1;
    }
    LOGD("egl init success! ");
    return 0;
}

int EGLHelper::swapBuffers() {
    if (mEglDisplay != EGL_NO_DISPLAY && mEglSurface != EGL_NO_SURFACE) {
        if (eglSwapBuffers(mEglDisplay, mEglSurface)) {
            return 0;
        }
    }
    return -1;
}

void EGLHelper::destroyEgl() {
    if (mEglDisplay != EGL_NO_DISPLAY) {
        eglMakeCurrent(mEglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    }
    if (mEglDisplay != EGL_NO_DISPLAY && mEglSurface != EGL_NO_SURFACE) {
        eglDestroySurface(mEglDisplay, mEglSurface);
        mEglSurface = EGL_NO_SURFACE;
    }
    if (mEglDisplay != EGL_NO_DISPLAY && mEglContext != EGL_NO_CONTEXT) {
        eglDestroyContext(mEglDisplay, mEglContext);
        mEglContext = EGL_NO_CONTEXT;
    }
    if (mEglDisplay != EGL_NO_DISPLAY) {
        eglTerminate(mEglDisplay);
        mEglDisplay = EGL_NO_DISPLAY;
    }
}
