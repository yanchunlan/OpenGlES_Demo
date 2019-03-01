//
// Created by pc on 2019/2/27.
//

#ifndef OPENGLES_DEMO_EGLHELPER_H
#define OPENGLES_DEMO_EGLHELPER_H


#include <EGL/egl.h>

class EGLHelper {

public:

    EGLDisplay eglDisplay;
    EGLSurface eglSurface;
    EGLConfig eglConfig;
    EGLContext eglContext;


public:

    EGLHelper();

    virtual ~EGLHelper();

    int initEgl(EGLNativeWindowType win);

    int swapBuffer();

    void destroyEgl();

};

#endif //OPENGLES_DEMO_EGLHELPER_H
