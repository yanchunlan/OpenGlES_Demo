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


/**
 * 具体流程是：
 *          openGL (包装类，不书写具体的实现，主要书写EGLThread,surface,子类shadler的业务逻辑)->
 *          baseOpenGL（所有shader控制的基类） ->
 *          filterOne/filterTwo/filterYUV 子类是具体ide实现，根据不同情况去切换子类
 *
 *      执行流程：
 *          opengl创建 -> eglThread创建 -> 开启线程 -> 执行baseOpengl创建
 *          当surfaceChange执行 -> eglThread changeSurface ,ondraw -> 执行baseOpengl changeSurface , draw
 *          当surfaceDsetory 执行 -> eglThread destory,并退出shader资源及输入的纹理资源
 *
 *      注意：
 *          全程baseOpenGl的具体实现，及释放都是再egl线程里面执行，利用函数的回调完成
 *          资源的释放，shader资源需要在egl子线程里面释放，并且其它输入的资源也需要在malloc,new 的页面释放
 *
 *      切换图片：重置图片data数据和矩阵matrix,并notifyRender
 *      切换滤镜：重置BaseOpengl子类（即更改shader的实现方案），执行其create,change,setpilx,并notifyRender
 *
 */

Opengl *opengl = NULL;

extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_surfaceCreate(JNIEnv *env, jobject instance,
                                                              jobject surface, jboolean isYuv) {
    if (opengl == NULL) {
        opengl = new Opengl();
    }
    opengl->onCreateSurface(env, surface, isYuv);
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
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_surfaceChangeFilter(JNIEnv *env, jobject instance) {
    if (opengl != NULL) {
        opengl->onChangeFilter();
    }
}


extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_surfaceDestroy(JNIEnv *env, jobject instance) {
    if (opengl != NULL) {
        opengl->onDestroySurface();
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


extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_setYuvData(JNIEnv *env, jobject instance,
                                                           jbyteArray y_, jbyteArray u_,
                                                           jbyteArray v_, jint w, jint h) {
    jbyte *y = env->GetByteArrayElements(y_, NULL);
    jbyte *u = env->GetByteArrayElements(u_, NULL);
    jbyte *v = env->GetByteArrayElements(v_, NULL);

    if (opengl != NULL) {
        opengl->setYuvData(y, u, v, w, h);
    }

    env->ReleaseByteArrayElements(y_, y, 0);
    env->ReleaseByteArrayElements(u_, u, 0);
    env->ReleaseByteArrayElements(v_, v, 0);

    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
//        env->ExceptionClear();
        LOGE("调用异常了");

        jclass cls_exception = env->FindClass("java/lang/Exception");
        env->ThrowNew(cls_exception,"ndk 99 exception");
        return;
    }
}