//
// Created by windows on 2019/3/3.
//
#include "EGLThread.h"


EGLThread::EGLThread() {
    pthread_mutex_init(&pthread_mutex, NULL);
    pthread_cond_init(&pthread_cond, NULL);
}

EGLThread::~EGLThread() {
    pthread_mutex_destroy(&pthread_mutex);
    pthread_cond_destroy(&pthread_cond);
}

// 在线程里面绘制
void *eglThreadImpl(void *context) {
    EGLThread *eglThread = static_cast<EGLThread *>(context);

    if (eglThread != NULL) {
        EGLHelper *eglHelper = new EGLHelper();
        eglHelper->initEgl(eglThread->nativeWindow);
        eglThread->isExit = false;

        while (true) {

            if (eglThread->isCreate) {
                LOGD("eglthread call surfaceCreate");
                eglThread->isCreate = false;
                eglThread->onCreate(eglThread->onCreateCtx);

            }


            if (eglThread->isChange) {
                LOGD("eglthread call surfaceChange");
                eglThread->isChange = false;
                eglThread->onChange(eglThread->surfaceWidth,
                                    eglThread->surfaceHeight,
                                    eglThread->onChangeCtx);

//                glViewport(0, 0, eglThread->surfaceWidth, eglThread->surfaceHeight);
                eglThread->isStart = true;

            }

            LOGD("draw"); // 绘制流程
            if (eglThread->isStart) {
//                glClearColor(0f, 1f, 1f, 1f);
//                glClear(GL_COLOR_BUFFER_BIT);

                eglThread->onDraw(eglThread->onDrawCtx);
                eglHelper->swapBuffers();
            }


            if (eglThread->renderType == OPENGL_RENDER_AUTO) {

                usleep(1000000 / 60);
            } else {
                // 阻塞
                pthread_mutex_lock(&eglThread->pthread_mutex);
                pthread_cond_wait(&eglThread->pthread_cond,&eglThread->pthread_mutex);
                pthread_mutex_unlock(&eglThread->pthread_mutex);
            }

            if (eglThread->isExit) {
                break;
            }

        }

    }

    // 代表线程结束
//    pthread_exit(&wlEglThread->eglThread);
    return 0;

};


void EGLThread::onSurfaceCreate(EGLNativeWindowType windowType) {

    if (eglThread == -1) {
        isCreate = true;
        nativeWindow = windowType;

        pthread_create(&eglThread, NULL, eglThreadImpl, this);
    }

}

void EGLThread::onSurfaceChange(int width, int height) {
    isChange = true;
    surfaceWidth = width;
    surfaceHeight = height;

    notifyRender(); // 设置宽高之后，就需要刷新一次
}

void EGLThread::callBackOnCreate(EGLThread::OnCreate onCreate, void *ctx) {
    this->onCreate = onCreate;
    this->onCreateCtx = ctx;
}

void EGLThread::callBackOnChange(EGLThread::OnChange onChange, void *ctx) {
    this->onChange = onChange;
    this->onChangeCtx = ctx;
}

void EGLThread::callBackOnDraw(EGLThread::OnDraw onDraw, void *ctx) {
    this->onDraw = onDraw;
    this->onDrawCtx = ctx;
}

void EGLThread::setRenderType(int renderType) {
    this->renderType = renderType;
}

void EGLThread::notifyRender() {
    pthread_mutex_lock(&pthread_mutex);
    pthread_cond_signal(&pthread_cond);
    pthread_mutex_unlock(&pthread_mutex);
}
