//
// Created by Administrator on 2019/4/20 0020.
//

#ifndef OPENGLES_DEMO_FILTERTWO_H
#define OPENGLES_DEMO_FILTERTWO_H


#include "BaseOpengl.h"
#include "../shaderutils/ShaderUtil.h"
#include "../matrix/MatrixUtil.h"


class FilterTwo: public  BaseOpengl {

public:
    GLint vPosition;
    GLint fPosition;
    GLint sampler;
    GLint u_matrix;
    GLuint textureId;

    int w;
    int h;
    void *pixels = NULL;

    float matrix[16];

public:
    FilterTwo();

    ~FilterTwo();

    void onCreate();

    void onChange(int w, int h);

    void draw();

    void destroy();

    void destroySource();

    void setMatrix(int width, int height);

    void setPilex(void *data, int width, int height, int length);

};



#endif //OPENGLES_DEMO_FILTERTWO_H
