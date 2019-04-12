//
// Created by pc on 2019/4/12.
//

#include "BaseOpengl.h"

BaseOpengl::BaseOpengl() {
    vertexs = new float[8];
    fragments = new float[8];


    float v[] = {1, -1,
                 1, 1,
                 -1, -1,
                 -1, 1};
    memcpy(vertexs, v, sizeof(v));

    float f[] = {1, 1,
                 1, 0,
                 0, 1,
                 0, 0};
    memcpy(fragments, f, sizeof(f));

}

BaseOpengl::~BaseOpengl() {
    delete[]vertexs;
    delete[]fragments;
}

void BaseOpengl::onCreate() {

}

void BaseOpengl::onChange(int width, int height) {

}

void BaseOpengl::draw() {

}

void BaseOpengl::destroy() {

}

void BaseOpengl::setPilex(void *data, int width, int height, int length) {

}
