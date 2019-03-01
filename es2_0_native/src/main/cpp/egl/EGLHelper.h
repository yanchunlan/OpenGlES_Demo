//
// Created by pc on 2019/2/27.
//

#ifndef OPENGLES_DEMO_EGLHELPER_H
#define OPENGLES_DEMO_EGLHELPER_H


#include <EGL/egl.h>
#include "../log/AndroidLog.h"

class EGLHelper {

public:

    EGLDisplay mEglDisplay;
    EGLSurface mEglSurface;
    EGLConfig mEglConfig;
    EGLContext mEglContext;


public:

    EGLHelper();

    virtual ~EGLHelper();

    int initEgl(EGLNativeWindowType win);

    int swapBuffers();

    void destroyEgl();

};

#endif //OPENGLES_DEMO_EGLHELPER_H
