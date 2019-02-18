//
// Created by windows on 2019/2/17.
//

#ifndef OPENGLES_DEMO_QUEUE_H
#define OPENGLES_DEMO_QUEUE_H


#include <queue>
#include <pthread.h>
#include <AndroidLog.h>
#include "librtmp/rtmp.h"


class Queue {
public:
    std::queue<RTMPPacket *> queuePacket;
    pthread_cond_t condPacket;
    pthread_mutex_t mutexPacket;

public:
    Queue();

    ~Queue();

    int putRtmpPacket(RTMPPacket *packet);

    RTMPPacket *getRtmpPacket();

    void clearQueue();

    void notifyQueue(); // 通知是放置柱塞，释放线程锁
};


#endif //OPENGLES_DEMO_QUEUE_H
