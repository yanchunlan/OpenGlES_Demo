//
// Created by ycl on 2018-11-12.
//
#pragma once
#ifndef WLPLAYER_ANDROIDLOG_H
#define WLPLAYER_ANDROIDLOG_H

#include <android/log.h>


#define LOGD(FORMAT,...) __android_log_print(ANDROID_LOG_DEBUG,"ycl",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"ycl",FORMAT,##__VA_ARGS__);

#endif //WLPLAYER_ANDROIDLOG_H
