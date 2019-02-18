//
// Created by windows on 2019/2/17.
//

#ifndef OPENGLES_DEMO_RTMPPUSH_H
#define OPENGLES_DEMO_RTMPPUSH_H

#include <malloc.h>
#include <string>
#include <pthread.h>
#include "librtmp/rtmp.h"
#include "Queue.h"
#include "AndroidLog.h"
#include "CallJava.h"

class RtmpPush {
public:
    RTMP *rtmp = NULL;
    char *url = NULL; // 外部jni里面创建了一个内存存储path,后面会释放，但是c里面如果还在用，就需要在创建内存存储，防止影响
    Queue *queue = NULL; // 置位null,防止野指针
    pthread_t push_thread_t;
    CallJava *callJava = NULL;// 回调
    bool startPushing = false;
    long startTime = 0;

public:
    RtmpPush(const char *url, CallJava *callJava);

    ~RtmpPush(); // 释放，类似于destroy

    void init();

    void pushSPSPPS(char *sps, int sps_len, char *pps, int pps_len);

    void pushVideoData(char *data, int data_len, bool keyframe);

    void pushAudioData(char *data, int data_len);

    void pushStop();
};


#endif //OPENGLES_DEMO_RTMPPUSH_H
