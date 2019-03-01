#include <jni.h>
#include <string>

#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <android/native_window.h>


#include "egl/EGLHelper.h"
#include "android/native_window.h"
#include "android/native_window_jni.h"
#include "GLES2/gl2.h"

EGLHelper *eglHelper = NULL;
ANativeWindow *nativeWindow = NULL;

extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_surfaceCreate(JNIEnv *env, jobject instance,jobject surface) {


    // init
    eglHelper = new EGLHelper();
    nativeWindow = ANativeWindow_fromSurface(env, surface);
    eglHelper->initEgl(nativeWindow);

    // openGL
    glViewport(0, 0, 720, 1280);
    glClearColor(0f, 0f, 0f, 1f);
    glClear(GL_COLOR_BUFFER_BIT);


    // 之前已经在后台创建了
    // 切换一次，把数据从后台切到前台
    eglHelper->swapBuffers();

}