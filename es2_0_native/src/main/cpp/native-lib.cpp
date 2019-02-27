#include <jni.h>
#include <string>

#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <android/native_window.h>



extern "C"
JNIEXPORT jstring JNICALL
Java_com_ycl_es2_10_1native_NativeActivity_stringFromJNI(JNIEnv *env, jobject instance) {



    std::string hello = "Hello from openGL";
    return env->NewStringUTF(hello.c_str());
}