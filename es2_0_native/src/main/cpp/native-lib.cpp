#include <jni.h>
#include <string>

#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <android/native_window.h>


#include "egl/EGLHelper.h"
#include "android/native_window.h"
#include "android/native_window_jni.h"
#include "GLES2/gl2.h"
#include "egl/EGLThread.h"

ANativeWindow *nativeWindow = NULL;
EGLThread *eglThread = NULL;


void callback_SurfaceCrete(void *ctx){
    LOGD("callback_SurfaceCrete");
    EGLThread *wlEglThread = static_cast<EGLThread *>(ctx);

};


void callback_SurfacChange(int w, int h, void *ctx){
    LOGD("callback_SurfacChange");
    EGLThread *wlEglThread = static_cast<EGLThread *>(ctx);

    glViewport(0, 0, w, h);
};


void callback_SurfaceDraw(void *ctx){
    LOGD("callback_SurfaceDraw");
    EGLThread *wlEglThread = static_cast<EGLThread *>(ctx);

    glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
};



extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_surfaceCreate(JNIEnv *env, jobject instance,jobject surface) {


    // init
    nativeWindow = ANativeWindow_fromSurface(env, surface); // 获取到jni里面的surface
    eglThread = new EGLThread();
    eglThread->setRenderType(OPENGL_RENDER_HANDLE);
    eglThread->callBackOnCreate(callback_SurfaceCrete, eglThread);
    eglThread->callBackOnChange(callback_SurfacChange, eglThread);
    eglThread->callBackOnDraw(callback_SurfaceDraw, eglThread);

    eglThread->onSurfaceCreate(nativeWindow);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_surfaceChange(JNIEnv *env, jobject instance,
                                                              jint width, jint height) {
    if (eglThread != NULL) {
        eglThread->onSurfaceChange(width, height);


        // 测试 notifyRender 是否有效，开启后就draw了2次
        usleep(1000000);
        eglThread->notifyRender();
    }
}