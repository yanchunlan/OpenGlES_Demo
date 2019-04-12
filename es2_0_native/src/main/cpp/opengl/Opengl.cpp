//
// Created by pc on 2019/4/12.
//


#include "Opengl.h"


Opengl::Opengl() {

}

Opengl::~Opengl() {

}

void callback_SurfaceCrete(void *ctx) {
    Opengl *opengl = static_cast<Opengl *>(ctx);
    if (opengl != NULL) {
        if (opengl->baseOpengl != NULL) {
            opengl->baseOpengl->onCreate();
        }
    }
}
void callback_SurfaceChange(int width, int height, void *ctx) {
    Opengl *opengl = static_cast<Opengl *>(ctx);
    if (opengl != NULL) {
        if (opengl->baseOpengl != NULL) {
            opengl->baseOpengl->onChange(width,height);
        }
    }
}
void callback_SurfaceDraw(void *ctx) {
    Opengl *opengl = static_cast<Opengl *>(ctx);
    if (opengl != NULL) {
        if (opengl->baseOpengl != NULL) {
            opengl->baseOpengl->draw();
        }
    }
}

void Opengl::onCreateSurface(JNIEnv *env, jobject surface) {
    // init
    nativeWindow = ANativeWindow_fromSurface(env, surface); // 获取到jni里面的surface
    eglThread = new EGLThread();
    eglThread->setRenderType(OPENGL_RENDER_HANDLE);

    eglThread->callBackOnCreate(callback_SurfaceCrete, this);
    eglThread->callBackOnChange(callback_SurfaceChange, this);
    eglThread->callBackOnDraw(callback_SurfaceDraw, this);


    baseOpengl = new FilterOne();

    eglThread->onSurfaceCreate(nativeWindow);
}

void Opengl::onChangeSurface(int width, int height) {
    if (eglThread != NULL) {
        if (baseOpengl != NULL) {
            baseOpengl->surface_width = width;
            baseOpengl->surface_height = height;
        }
        eglThread->onSurfaceChange(width, height);
    }
}

void Opengl::onDestroySurface() {
    if (eglThread != NULL) {
        eglThread->destroy();
        delete eglThread;
        eglThread = NULL;
    }
    if (baseOpengl != NULL) {
        baseOpengl->destroy();
        delete baseOpengl;
        baseOpengl = NULL;
    }
    if (nativeWindow != NULL) {
        ANativeWindow_release(nativeWindow);
        nativeWindow = NULL;
    }
}

void Opengl::setPilex(void *data, int width, int height, int length) {
    this->pic_width = width;
    this->pic_height = height;
    pilex = malloc(length);
    memcpy(pilex, data, length);
    if (baseOpengl != NULL) {
        baseOpengl->setPilex(data, width, height, length);
    }
    if (eglThread != NULL) {
        eglThread->notifyRender();
    }
}
