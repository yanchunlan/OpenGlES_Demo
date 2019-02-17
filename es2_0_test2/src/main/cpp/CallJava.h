//
// Created by windows on 2019/2/17.
//

#ifndef OPENGLES_DEMO_CALLJAVA_H
#define OPENGLES_DEMO_CALLJAVA_H


#include <jni.h>


#define THREAD_MAIN 1
#define THREAD_CHILD 2


class CallJava {
public:
    JNIEnv *jniEnv = NULL; // 主线程需要jnienv去获取java的通信
    JavaVM *javaVM = NULL;// 子线程需要javaVM
    jobject jobj;

    jmethodID jmid_connecting;
    jmethodID jmid_connectSuccess;
    jmethodID jmid_connectFail;


public:
    CallJava(JavaVM *javaVM, JNIEnv *jniEnv, jobject jobj);

    ~CallJava();

    void onConnecting(int type); // 连接不确定是不是主线程，子线程，所以2种状态都可能调用到

    // 成功或者失败一定是子线程
    void onConnectSuccess();

    void onConnectFail(char *msg);

};

#endif //OPENGLES_DEMO_CALLJAVA_H
