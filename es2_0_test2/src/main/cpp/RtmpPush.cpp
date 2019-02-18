//
// Created by windows on 2019/2/17.
//

#include "RtmpPush.h"


RtmpPush::RtmpPush(const char *url, CallJava *callJava) {
    // char 类型存在堆中
    this->url = static_cast<char *>(malloc(512));
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
    rtmpPush->startPushing = false;


    rtmpPush->rtmp = RTMP_Alloc();
    RTMP_Init(rtmpPush->rtmp);
    // 设置超时，直播流
    rtmpPush->rtmp->Link.timeout = 10; // 10s
    rtmpPush->rtmp->Link.lFlags |= RTMP_LF_LIVE; // 追加 表示是直播流

    RTMP_SetupURL(rtmpPush->rtmp, rtmpPush->url);
    RTMP_EnableWrite(rtmpPush->rtmp);

    if (!RTMP_Connect(rtmpPush->rtmp, NULL)) {
        LOGE("can not connect the url");
        std::string msg = "can not connect the url";
        rtmpPush->callJava->onConnectFail(const_cast<char *>(msg.c_str()));
        goto end;
    }

    if (!RTMP_ConnectStream(rtmpPush->rtmp, 0)) {
        LOGE("can not connect the stream of service");
        std::string msg = "can not connect the stream of service";
        rtmpPush->callJava->onConnectFail(const_cast<char *>(msg.c_str()));
        goto end;
    }

//    成功 开始推流
    LOGD("链接成功， 开始推流");
    rtmpPush->callJava->onConnectSuccess();

    rtmpPush->startPushing = true;
    rtmpPush->startTime = RTMP_GetTime(); // 得到RTMP的初始值，后面计算减去落差

// 推流逻辑 ,从队列去除数据，推流到服务器
    while (true) {
        if (!rtmpPush->startPushing) {
            break;
        }
        RTMPPacket *packet = NULL;
        packet = rtmpPush->queue->getRtmpPacket();
        if (packet != NULL) {
            int result = RTMP_SendPacket(rtmpPush->rtmp, packet, 1); // 一次只推流一个
            LOGD("RTMP_SendPacket result is %d", result);
//          推流完成释放packet资源
            RTMPPacket_Free(packet);
            free(packet);
            packet = NULL;
        }
    }

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


void RtmpPush::pushSPSPPS(char *sps, int sps_len, char *pps, int pps_len) {
    int bodysize = sps_len + pps_len + 16; // sps , pps 都是严格按照其格式存储

    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, bodysize);
    RTMPPacket_Reset(packet);

    char *body = packet->m_body;

    int i = 0;

    body[i++] = 0x17;

    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;

    body[i++] = 0x01;
    body[i++] = sps[1];
    body[i++] = sps[2];
    body[i++] = sps[3];

    body[i++] = 0xFF;

    body[i++] = 0xE1;
    body[i++] = (sps_len >> 8) & 0xff; // 此处有移位，其实就是移动空间位置，前面显示前面的一半，后面显示后面的一半
    body[i++] = sps_len & 0xff;
    memcpy(&body[i], sps, sps_len); // 裸数据
    i += sps_len;

    body[i++] = 0x01;
    body[i++] = (pps_len >> 8) & 0xff;
    body[i++] = pps_len & 0xff;
    memcpy(&body[i], pps, pps_len);

    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = bodysize;
    packet->m_nTimeStamp = 0; // sps,pps不需要时间轴
    packet->m_hasAbsTimestamp = 0;
    packet->m_nChannel = 0x04;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;// 数据不大，所以用medium即可
    packet->m_nInfoField2 = rtmp->m_stream_id;

    queue->putRtmpPacket(packet);


}

void RtmpPush::pushVideoData(char *data, int data_len, bool keyframe) {
    int bodysize = data_len + 9; // h.264 avc 加9个byte
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, bodysize);
    RTMPPacket_Reset(packet);

    char *body = packet->m_body;
    int i = 0;

    if (keyframe) // 关键帧与非关键帧数据不同
    {
        body[i++] = 0x17;
    } else {
        body[i++] = 0x27;
    }

    body[i++] = 0x01;  // 这里是null单元 跟上面不同
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;

    body[i++] = (data_len >> 24) & 0xff;
    body[i++] = (data_len >> 16) & 0xff;
    body[i++] = (data_len >> 8) & 0xff;
    body[i++] = data_len & 0xff;
    memcpy(&body[i], data, data_len);

    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = bodysize;
    packet->m_nTimeStamp = RTMP_GetTime() - startTime; // 减去初始值，需要加时间轴
    packet->m_hasAbsTimestamp = 0;
    packet->m_nChannel = 0x04;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;// 数据大，所以用large
    packet->m_nInfoField2 = rtmp->m_stream_id;

    queue->putRtmpPacket(packet);
}

void RtmpPush::pushAudioData(char *data, int data_len) {
    int bodysize = data_len + 2; // aac是只加2个byte
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, bodysize);
    RTMPPacket_Reset(packet);
    char *body = packet->m_body;
    // 前4位的数值表示了音频数据格式 ---- 10(A)表示 AAC
    //第5-6位的数值表示采样率，0 = 5.5 kHz，1 = 11 kHz，2 = 22 kHz，3(11) = 44 kHz。
    //第7位表示采样精度，0 = 8bits，1 = 16bits。
    //第8位表示音频类型，0 = mono，1 = stereo
    // 10  1111  == 0xAF
    body[0] = 0xAF;
    // 0x00 aac头信息  
    // 0x01 aac 原始数据
    body[1] = 0x01;
    memcpy(&body[2], data, data_len); // 后面放置裸数据

    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;  // 此处是Audio
    packet->m_nBodySize = bodysize;
    packet->m_nTimeStamp = RTMP_GetTime() - startTime;
    packet->m_hasAbsTimestamp = 0;
    packet->m_nChannel = 0x04;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nInfoField2 = rtmp->m_stream_id;
    queue->putRtmpPacket(packet);
}

void RtmpPush::pushStop() {
    startPushing = false;
    queue->notifyQueue(); // 防止卡住了
    pthread_join(push_thread_t, NULL);
}