//
// Created by pc on 2019/4/12.
//

#ifndef OPENGLES_DEMO_OPENGL_H
#define OPENGLES_DEMO_OPENGL_H

#include <jni.h>
#include <cstdlib>
#include "../egl/EGLThread.h"
#include "android/native_window.h"
#include "android/native_window_jni.h"
#include "BaseOpengl.h"
#include "FilterOne.h"

class Opengl{

public:
    EGLThread *eglThread = NULL;
    ANativeWindow *nativeWindow = NULL;
    BaseOpengl *baseOpengl = NULL;

    // params
    int pic_width;
    int pic_height;
    void *pilex = NULL; // 圖片的 data

public:
    Opengl();

    ~Opengl();

    void onCreateSurface(JNIEnv *env, jobject surface);

    void onChangeSurface(int width, int height);

    void onDestroySurface();

    void setPilex(void *data, int width, int height, int length);

};


#endif //OPENGLES_DEMO_OPENGL_H
