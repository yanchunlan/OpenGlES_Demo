//
// Created by windows on 2019/2/17.
//

#include "RtmpPush.h"


RtmpPush::RtmpPush(const char *url, CallJava *callJava) {
    // char 类型存在堆中

    this->url = static_cast<char *> (malloc(512));
    strcpy(this->url, url);
    this->queue = new Queue();
    this->callJava = callJava;
}


RtmpPush::~RtmpPush() {
    queue->notifyQueue(); // 解除柱塞
    queue->clearQueue();
    free(url); // 释放开辟的内存
}


void *callBackPush(void *data) {

    RtmpPush *rtmpPush = static_cast<RtmpPush *>(data); // 获取到当前指针，因为是子线程，所以需要传递进去

//    rtmpPush->callJava->onConnecting(THREAD_CHILD);

    rtmpPush->rtmp = RTMP_Alloc();
    RTMP_Init(rtmpPush->rtmp);
    // 设置超时，直播流
    rtmpPush->rtmp->Link.timeout = 10; // 10s
    rtmpPush->rtmp->Link.lFlags |= RTMP_LF_LIVE; // 追加 表示是直播流

    RTMP_SetupURL(rtmpPush->rtmp, rtmpPush->url);
    RTMP_EnableWrite(rtmpPush->rtmp);

    if (!RTMP_Connect(rtmpPush->rtmp, NULL)) {
//        LOGE("can not connect the url");
        rtmpPush->callJava->onConnectFail("can not connect the url");
        goto end;
    }

    if (!RTMP_ConnectStream(rtmpPush->rtmp, 0)) {
//        LOGE("can not connect the stream of service");
        rtmpPush->callJava->onConnectFail("can not connect the stream of service");
        goto end;
    }
//    LOGD("链接成功， 开始推流");
    rtmpPush->callJava->onConnectSuccess();


    // 执行异常，直接退出线程, end 是随便取的一个参数
    end:
    RTMP_Close(rtmpPush->rtmp); // 里面也有判断rtmp是否连接
    RTMP_Free(rtmpPush->rtmp);
    rtmpPush->rtmp = NULL;
    pthread_exit(&rtmpPush->push_thread_t);
};

void RtmpPush::init() {
    callJava->onConnecting(THREAD_MAIN);
    // 创建线程，在线程里面执行rtmp初始化 ， 回调返回coid* ,内部也返回*,后面的this,就是参数
    pthread_create(&push_thread_t, NULL, callBackPush, this);
}
