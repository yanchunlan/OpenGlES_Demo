//
// Created by pc on 2019/4/12.
//

#include <GLES2/gl2.h>

#ifndef OPENGLES_DEMO_BASEOPENGL_H
#define OPENGLES_DEMO_BASEOPENGL_H

#include <GLES2/gl2.h>
#include <cstring>
#include "../log/AndroidLog.h"

class BaseOpengl {

    // 共有的参数
public:
    int surface_width;
    int surface_height;

    // shader
    char *vertex;
    char *fragment;

    // 坐标
    float *vertexs;
    float *fragments;

    GLuint program;
    // 缓存shader主要是方便删除缓存
    GLuint vShader;
    GLuint fShader;

public:

    BaseOpengl();

    ~BaseOpengl();

    virtual void onCreate();

    virtual void onChange(int width, int height);

    virtual void draw();

    virtual void destroy();// 释放shader相关资源

    virtual void destroySource(); // 释放数据相关的资源

    virtual void setPilex(void *data, int width, int height, int length);

    virtual void setYuvData(void *y, void *u, void *v, int width, int height);

};


#endif //OPENGLES_DEMO_BASEOPENGL_H