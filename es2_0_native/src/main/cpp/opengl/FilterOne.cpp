//
// Created by pc on 2019/4/12.
//

#include "FilterOne.h"

FilterOne::FilterOne() {

}

FilterOne::~FilterOne() {

}

void FilterOne::onCreate() {
    BaseOpengl::onCreate();
}

void FilterOne::onChange(int w, int h) {
    BaseOpengl::onChange(w, h);
}

void FilterOne::draw() {
    BaseOpengl::draw();
}

void FilterOne::setMatrix(int width, int height) {

}

void FilterOne::setPilex(void *data, int width, int height, int length) {
    BaseOpengl::setPilex(data, width, height, length);
}
