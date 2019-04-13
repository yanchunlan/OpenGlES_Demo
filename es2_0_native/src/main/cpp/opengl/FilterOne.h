//
// Created by pc on 2019/4/12.
//

#ifndef OPENGLES_DEMO_FILTERONE_H
#define OPENGLES_DEMO_FILTERONE_H


#include "BaseOpengl.h"
#include "../shaderutils/ShaderUtil.h"
#include "../matrix/MatrixUtil.h"


class FilterOne: public  BaseOpengl {

public:
    GLint vPosition;
    GLint fPosition;
    GLint sampler;
    GLuint textureId;
    GLint u_matrix;

    int w;
    int h;
    void *pixels = NULL;

    float matrix[16];

public:
    FilterOne();

    ~FilterOne();

    void onCreate();

    void onChange(int w, int h);

    void draw();

    void setMatrix(int width, int height);

    void setPilex(void *data, int width, int height, int length);

};
#endif //OPENGLES_DEMO_FILTERONE_H
