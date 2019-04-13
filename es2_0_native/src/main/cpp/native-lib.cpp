#include <jni.h>
#include <string>
#include <malloc.h>
#include <cstring>

#include "android/native_window.h"
#include "android/native_window_jni.h"
#include "GLES2/gl2.h"
#include "egl/EGLThread.h"
#include "shaderutils/ShaderUtil.h"
#include "matrix/MatrixUtil.h"
#include "opengl/Opengl.h"


Opengl *opengl = NULL;

extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_surfaceCreate(JNIEnv *env, jobject instance,
                                                              jobject surface) {
    if (opengl == NULL) {
        opengl = new Opengl();
    }
    opengl->onCreateSurface(env, surface);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_surfaceChange(JNIEnv *env, jobject instance,
                                                              jint width, jint height) {
    if (opengl != NULL) {
        opengl->onChangeSurface(width, height);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_surfaceDestroy(JNIEnv *env, jobject instance) {
    if (opengl != NULL) {
        opengl->onDestroySurface( );
        delete opengl;
        opengl = NULL;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_imgData(JNIEnv *env, jobject instance, jint width,
                                                        jint height, jint length,
                                                        jbyteArray data_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    if (opengl != NULL) {
        opengl->setPilex(data, width, height, length);
    }
    env->ReleaseByteArrayElements(data_, data, 0);
}