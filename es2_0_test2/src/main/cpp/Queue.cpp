//
// Created by windows on 2019/2/17.
//

#include "Queue.h"


Queue::Queue() {
    pthread_mutex_init(&mutexPacket, NULL); // 每个方法都需要锁住，释放完成之后再释放锁
    pthread_cond_init(&condPacket, NULL);
}

// 退出释放
Queue::~Queue() {
    clearQueue();
    pthread_mutex_destroy(&mutexPacket);
    pthread_cond_destroy(&condPacket);
}

int Queue::putRtmpPacket(RTMPPacket *packet) {
    pthread_mutex_lock(&mutexPacket);
    queuePacket.push(packet);
    pthread_cond_signal(&condPacket);  // 主要是get方法没有数据的时候就锁住了，现在放入数据就释放那边的条件锁
    pthread_mutex_unlock(&mutexPacket);
    return 0;
}

RTMPPacket *Queue::getRtmpPacket() {
    pthread_mutex_lock(&mutexPacket);

    RTMPPacket *p = NULL;
    if(!queuePacket.empty())
    {
        p = queuePacket.front();
        queuePacket.pop();
    } else{
        pthread_cond_wait(&condPacket, &mutexPacket);
    }
    pthread_mutex_unlock(&mutexPacket);
    return p;
}

// 清楚队列数据，并释放队列里面的对象内存
void Queue::clearQueue() {

    pthread_mutex_lock(&mutexPacket);
    while(true)
    {
        if(queuePacket.empty())
        {
            break;
        }
        RTMPPacket *p = queuePacket.front();
        queuePacket.pop();
        RTMPPacket_Free(p);
        p = NULL;
    }
    pthread_mutex_unlock(&mutexPacket);

}

void Queue::notifyQueue() {

    pthread_mutex_lock(&mutexPacket);
    pthread_cond_signal(&condPacket);
    pthread_mutex_unlock(&mutexPacket);

}
