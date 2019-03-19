//
// Created by pc on 2019/3/19.
//

#ifndef OPENGLES_DEMO_MATRIXUTIL_H
#define OPENGLES_DEMO_MATRIXUTIL_H

#include <math.h>

// 单位矩阵
static void initMatrix(float *matrix) {
    for (int i = 0; i < 16; i++) {
        if (i % 5 == 0) {
            matrix[i] = 1;
        } else {
            matrix[i] = 0;
        }
    }
}

//  xy 绕 z 旋转
static void rotateMatrix(double angle, float *matrix) {
    angle = angle * (M_PI / 180.0);

    matrix[0] = cos(angle);
    matrix[1] = -sin(angle);
    matrix[4] = sin(angle);
    matrix[5] = cos(angle);

};

// 只缩放 xy   , 规律是: scale(x,y,z)
static void scaleMatrix(double scale, float *matrix) {
    matrix[0] = scale;
    matrix[5] = scale;
}

// 只平移 xy   , 规律是: (x+trans,y+trans,z+trans)
static void transMatrix(double x, double y, float *matrix) {
    matrix[3] = x;
    matrix[7] = y;
}

static void orthoM(float left, float right, float bottom, float top, float *matrix) {
    matrix[0] = 2 / (right - left);
    matrix[3] = (right + left) / (right - left) * -1;
    matrix[5] = 2 / (top - bottom);
    matrix[7] = (top + bottom) / (top - bottom) * -1;
    matrix[10] = 1;
    matrix[11] = 1;
};

#endif //OPENGLES_DEMO_MATRIXUTIL_H
