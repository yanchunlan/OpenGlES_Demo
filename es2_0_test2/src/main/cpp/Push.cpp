#include <jni.h>
#include <string>
#include "RtmpPush.h"

// c里面的对象
RtmpPush *rtmpPush = NULL;
CallJava *callJava = NULL;
bool exit = true;

// 虚拟机
JavaVM *javaVM = NULL; // 在onload的时候就有数据了

extern "C"
JNIEXPORT void JNICALL
Java_com_opengles_book_es2_10_1test2_push_PushVideo_initPush(JNIEnv *env, jobject instance,
                                                             jstring pushUrl_) {
    const char *pushUrl = env->GetStringUTFChars(pushUrl_, 0);

    // 避免多次初始化
    if (callJava == NULL) {
        exit = false;
        callJava = new CallJava(javaVM, env, instance);
        // rtmp 的初始化
        rtmpPush = new RtmpPush(pushUrl, callJava);
        rtmpPush->init();
    }

    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
//        env->ExceptionClear();
        LOGE("调用异常了");

        jclass cls_exception = env->FindClass("java/lang/Exception");
        env->ThrowNew(cls_exception,"ndk 34 exception");
        return;
    }

    env->ReleaseStringUTFChars(pushUrl_, pushUrl);


}


// 重写获取 javaVM 的方法
extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    if (LOG_SHOW) {
        LOGD("JNI_OnLoad");
    }
    // 确定返回是否成功
    JNIEnv *env;
    if (vm->GetEnv((void **) env, JNI_VERSION_1_4) != JNI_OK) {
        if (LOG_SHOW) {
            LOGE("GetEnv failed!");
        }
        return -1;
    }
    return JNI_VERSION_1_4;
};

extern "C"
JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *javaVM1, void *reserved) {
    javaVM = NULL;
    if (LOG_SHOW) {
        LOGD("JNI_OnUnload");
    }
};


extern "C"
JNIEXPORT void JNICALL
Java_com_opengles_book_es2_10_1test2_push_PushVideo_pushSPSPPS__Lbyte_3_093_2ILbyte_3_093_2I(
        JNIEnv *env, jobject instance, jbyteArray sps_, jint sps_len, jbyteArray pps_,
        jint pps_len) {
    jbyte *sps = env->GetByteArrayElements(sps_, NULL);
    jbyte *pps = env->GetByteArrayElements(pps_, NULL);

    if (rtmpPush != NULL && !exit) {
        rtmpPush->pushSPSPPS(reinterpret_cast<char *>(sps), sps_len, reinterpret_cast<char *>(pps),
                             pps_len);
    }

    env->ReleaseByteArrayElements(sps_, sps, 0);
    env->ReleaseByteArrayElements(pps_, pps, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_opengles_book_es2_10_1test2_push_PushVideo_pushVideoData__Lbyte_3_093_2IZ(JNIEnv *env,
                                                                                   jobject instance,
                                                                                   jbyteArray data_,
                                                                                   jint data_len,
                                                                                   jboolean keyFrame) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    if (rtmpPush != NULL && !exit) {
        rtmpPush->pushVideoData(reinterpret_cast<char *>(data), data_len, keyFrame);
    }

    env->ReleaseByteArrayElements(data_, data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_opengles_book_es2_10_1test2_push_PushVideo_pushAudioData__Lbyte_3_093_2I(JNIEnv *env,
                                                                                  jobject instance,
                                                                                  jbyteArray data_,
                                                                                  jint data_len) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    if (rtmpPush != NULL && !exit) {
        rtmpPush->pushAudioData(reinterpret_cast<char *>(data), data_len);
    }

    env->ReleaseByteArrayElements(data_, data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_opengles_book_es2_10_1test2_push_PushVideo_pushStop(JNIEnv *env, jobject instance) {
    if (rtmpPush != NULL) {
        exit = true;
        rtmpPush->pushStop();
        delete (rtmpPush);
        delete (callJava);
        rtmpPush = NULL;
        callJava = NULL;
    }

    /*  if (env->ExceptionCheck()) {
          env->ExceptionDescribe();
          env->ExceptionClear();


      }*/
}

