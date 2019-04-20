//
// Created by Administrator on 2019/4/20 0020.
//

#ifndef OPENGLES_DEMO_FILTERYUV_H
#define OPENGLES_DEMO_FILTERYUV_H


#include "BaseOpengl.h"
#include "../shaderutils/ShaderUtil.h"
#include "../matrix/MatrixUtil.h"
#include <cstdlib>


class FilterYUV : public BaseOpengl {

public:
    GLint vPosition;
    GLint fPosition;
    GLint u_matrix;

    GLint sampler_y;
    GLint sampler_u;
    GLint sampler_v;

    GLuint textureIds[3];

    float matrix[16];

    // 存储数据
    void *y = NULL;
    void *u = NULL;
    void *v = NULL;
    int yuv_w = 0;
    int yuv_h = 0;

public:
    FilterYUV();

    ~FilterYUV();

    void onCreate();

    void onChange(int w, int h);

    void draw();

    void destroy();

    void destroySource();

    void setMatrix(int width, int height);

    void setYuvData(void *y, void *u, void *v, int width, int height);
};


#endif //OPENGLES_DEMO_FILTERYUV_H
