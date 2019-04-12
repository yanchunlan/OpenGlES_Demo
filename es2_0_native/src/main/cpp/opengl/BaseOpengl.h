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

public:
    int surface_width;
    int surface_height;

    char *vertex;
    char *fragment;

    float *vertexs;
    float *fragments;

    GLuint program;

public:

    BaseOpengl();

    ~BaseOpengl();

    virtual void onCreate();

    virtual void onChange(int width, int height);

    virtual void draw();

    virtual void destroy();

    virtual void setPilex(void *data, int width, int height, int length);

};


#endif //OPENGLES_DEMO_BASEOPENGL_H