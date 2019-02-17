#include <jni.h>
#include <string>


#include "RtmpPush.h"

extern "C"
{
#include "librtmp/rtmp.h"
}


// c里面的对象
RtmpPush *rtmpPush = NULL;
CallJava *callJava = NULL;

// 虚拟机
JavaVM *javaVM = NULL; // 在onload的时候就有数据了


extern "C"
JNIEXPORT void JNICALL
Java_com_opengles_book_es2_10_1test2_push_PushVideo_initPush(JNIEnv *env, jobject instance,
                                                             jstring pushUrl_) {
    const char *pushUrl = env->GetStringUTFChars(pushUrl_, 0);

    callJava = new CallJava(javaVM,env,instance);
    // rtmp 的初始化
    rtmpPush = new RtmpPush(pushUrl, callJava);
    rtmpPush->init();

    env->ReleaseStringUTFChars(pushUrl_, pushUrl);
}

// 重写获取 javaVM 的方法
extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;

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
};
