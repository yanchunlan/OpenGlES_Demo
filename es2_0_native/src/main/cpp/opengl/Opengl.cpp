//
// Created by pc on 2019/4/12.
//


#include "Opengl.h"


Opengl::Opengl() {

}

Opengl::~Opengl() {

}

// -------------------------- callback start ---------------------

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
void callback_SurfaceChangeFilter(int width,int height,void *ctx) {
    Opengl *opengl = static_cast<Opengl *>(ctx);
    if (opengl != NULL) {
        // 切换滤镜的处理，实际就是切换shader，先释放前面的shader,再重新加载新的shader
        // 即 baseOpengl 的重新设置
        if (opengl->baseOpengl != NULL) {
            opengl->baseOpengl->destroy();
            opengl->baseOpengl->destroySource();
            delete opengl->baseOpengl;
            opengl->baseOpengl = NULL;
        }
        opengl->baseOpengl = new FilterTwo();
        opengl->baseOpengl->onCreate();
        opengl->baseOpengl->onChange(width, height);
        opengl->baseOpengl->setPilex(opengl->pilex, opengl->pic_width,opengl->pic_height,opengl->pilex_length);
        if (opengl->eglThread != NULL) {
            opengl->eglThread->notifyRender();
        }
    }
}
void callback_SurfaceDestroy(void *ctx) {
    Opengl *opengl = static_cast<Opengl *>(ctx);
    if (opengl != NULL) {
        if (opengl->baseOpengl != NULL) {
            opengl->baseOpengl->destroy();
        }
    }
}
// -------------------------- callback end ---------------------


void Opengl::onCreateSurface(JNIEnv *env, jobject surface) {
    // init
    baseOpengl = new FilterOne();
    nativeWindow = ANativeWindow_fromSurface(env, surface); // 获取到jni里面的surface

    // 先设置监听，在执行创建方法
    eglThread = new EGLThread();
    eglThread->setRenderType(OPENGL_RENDER_HANDLE);

    eglThread->callBackOnCreate(callback_SurfaceCrete, this);
    eglThread->callBackOnChange(callback_SurfaceChange, this);
    eglThread->callBackOnDraw(callback_SurfaceDraw, this);
    eglThread->callBackOnChangeFilter(callback_SurfaceChangeFilter, this);
    eglThread->callBackOnDestroy(callback_SurfaceDestroy, this);

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

void Opengl::onChangeFilter() {
    if (eglThread != NULL) {
        eglThread->onSurfaceChangeFilter();
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
        baseOpengl->destroySource();
        delete baseOpengl;
        baseOpengl = NULL;
    }
    if (nativeWindow != NULL) {
        ANativeWindow_release(nativeWindow);
        nativeWindow = NULL;
    }
    if (pilex != NULL) { // 因为malloc了 ，所以需要free
        free(pilex);
        pilex = NULL;
    }
}

void Opengl::setPilex(void *data, int width, int height, int length) {
    // 因为存在多次设置图片，所以当下有一次设置就释放前面的图片内存
    if (pilex != NULL) {
        free(pilex);
        pilex = NULL;
    }

    // imgs cache
    this->pic_width = width;
    this->pic_height = height;
    pilex = malloc(length);
    memcpy(pilex, data, length);
    this->pilex_length = length;

    // set params
    if (baseOpengl != NULL) {
        baseOpengl->setPilex(pilex, width, height, length);
    }
    if (eglThread != NULL) {
        eglThread->notifyRender();
    }
}


void Opengl::setYuvData(void *y, void *u, void *v, int w, int h) {
    if (baseOpengl != NULL) {
        baseOpengl->setYuvData(y,u,v,w,h);
    }
    if (eglThread != NULL) {
        eglThread->notifyRender();
    }
}
