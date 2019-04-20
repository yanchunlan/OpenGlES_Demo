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
#include "FilterTwo.h"
#include "FilterYUV.h"

/**
 * 类似于包装类，不做具体的实现，仅仅是调用各个业务的交互
 */
class Opengl{

public:
    EGLThread *eglThread = NULL;
    ANativeWindow *nativeWindow = NULL;
    BaseOpengl *baseOpengl = NULL;

    // params  : 为什么需要缓存图片数据，是因为当 baseOpenGL的子类释放的时候，需要重新绑定图片数据，所以需要缓存以前的数据，保证可以重新设置
    int pic_width;
    int pic_height;
    void *pilex = NULL; // 图片具体的 data
    int pilex_length;

public:
    Opengl();

    ~Opengl();

    void onCreateSurface(JNIEnv *env, jobject surface); // env 主要是获取surface

    void onChangeSurface(int width, int height);

    void onChangeFilter();

    void onDestroySurface();

    void setPilex(void *data, int width, int height, int length);

    void setYuvData(void *y, void *u, void *v, int w, int h);
};


#endif //OPENGLES_DEMO_OPENGL_H
