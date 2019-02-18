//
// Created by windows on 2019/2/17.
//

#include "CallJava.h"

CallJava::CallJava(JavaVM *javaVM, JNIEnv *jniEnv, jobject jobj) {
    this->javaVM = javaVM;
    this->jniEnv = jniEnv;
    this->jobj = jniEnv->NewGlobalRef(jobj); // 对象保存全局的需要创建全局对象,

    // 初始化方法id

    jclass jcls = jniEnv->GetObjectClass(this->jobj);
    jmid_connecting = jniEnv->GetMethodID(jcls, "onConnecting", "()V");

    jmid_connectSuccess = jniEnv->GetMethodID(jcls, "onConnectSuccess", "()V");

    jmid_connectFail = jniEnv->GetMethodID(jcls, "onConnectFail", "(Ljava/lang/String;)V");

}

void CallJava::onConnecting(int type) {
    if (type == THREAD_CHILD) { // 子线程调用方法
        JNIEnv *jniEnv;
        if (javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {// 绑定线程 ，javaVM里面获取JNIenv
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_connecting);
        javaVM->DetachCurrentThread(); // 解除线程
    } else {
        jniEnv->CallVoidMethod(jobj, jmid_connecting);
    }
}

void CallJava::onConnectSuccess() {
    JNIEnv *jniEnv;
    if (javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {// 绑定线程 ，javaVM里面获取JNIenv
        return;
    }
    jniEnv->CallVoidMethod(jobj, jmid_connectSuccess);
    javaVM->DetachCurrentThread(); // 解除线程

}

void CallJava::onConnectFail(char *msg) {
    JNIEnv *jniEnv;
    if (javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {// 绑定线程 ，javaVM里面获取JNIenv
        return;
    }
    // 失败了需要弹出失败的提示，传递出去是根据jni 的jString传递出去的
    jstring jmsg = jniEnv->NewStringUTF(msg);
    jniEnv->CallVoidMethod(jobj, jmid_connectFail, jmsg);
    jniEnv->DeleteLocalRef(jmsg);// 释放临时的jString

    javaVM->DetachCurrentThread(); // 解除线程
}

CallJava::~CallJava() {
    jniEnv->DeleteGlobalRef(jobj);
    javaVM = NULL;
    jniEnv = NULL;
    // 方法id不用管，只需要释放jniEnv=NULL即可
}
