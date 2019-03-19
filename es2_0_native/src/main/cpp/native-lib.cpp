#include <jni.h>
#include <string>
#include <malloc.h>
#include <cstring>

#include "android/native_window.h"
#include "android/native_window_jni.h"
#include "GLES2/gl2.h"
#include "egl/EGLThread.h"
#include "shaderutils/ShaderUtil.h"



ANativeWindow *nativeWindow = NULL;
EGLThread *eglThread = NULL;

const char *vertex = "attribute vec4 v_Position;\n"
                     "attribute vec2 f_Position;\n"
                     "varying vec2 ft_Position;\n"
                     "void main() {\n"
                     "    ft_Position = f_Position;\n"
                     "    gl_Position = v_Position;\n"
                     "}";

const char *fragment = "precision mediump float;\n"
                       "varying vec2 ft_Position;\n"
                       "uniform sampler2D sTexture;\n"
                       "void main() {\n"
                       "    gl_FragColor=texture2D(sTexture, ft_Position);\n"
                       "}";
int program;
GLint vPosition;
GLint fPosition;
GLint sampler;
GLuint textureId;

// params
int w;
int h;
void *pixels = NULL;

float vertexs[] = {
        1, -1,
        1, 1,
        -1, -1,
        -1, 1
};

float fragments[] = {
        1, 1,
        1, 0,
        0, 1,
        0, 0
};


void callback_SurfaceCrete(void *ctx) {
    LOGD("callback_SurfaceCrete");
    EGLThread *wlEglThread = static_cast<EGLThread *>(ctx);


    program = createProgrm(vertex, fragment);
    LOGD("opengl program is %d", program);
    vPosition = glGetAttribLocation(program, "v_Position");//顶点坐标 // 此处差点错误，导致异常程序
    fPosition = glGetAttribLocation(program, "f_Position");//纹理坐标  // 此处差点错误，导致异常程序
    sampler = glGetUniformLocation(program, "sTexture");//2D纹理

    LOGD("opengl vPosition is %d", vPosition);
    LOGD("opengl fPosition is %d", fPosition);
    LOGD("opengl sampler is %d", sampler);


    glGenTextures(1, &textureId);
    glBindTexture(GL_TEXTURE_2D, textureId);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // 环绕效果是 重复 GL_CLAMP_TO_EDGE 代表 单独一个
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // 放大缩小效果是 线性
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    if (pixels != NULL) {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
    }
    glBindTexture(GL_TEXTURE_2D, 0);
};


void callback_SurfacChange(int w, int h, void *ctx) {
    LOGD("callback_SurfacChange");
    EGLThread *wlEglThread = static_cast<EGLThread *>(ctx);

    glViewport(0, 0, w, h);
};


void callback_SurfaceDraw(void *ctx) {
    LOGD("callback_SurfaceDraw");
    EGLThread *wlEglThread = static_cast<EGLThread *>(ctx);

    // 一般需要清屏 之后会加速后面的绘制
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    // 三角形
//    glUseProgram(program);
//    glEnableVertexAttribArray(vPosition);
//    glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 8, vertexs); // xy 2维度 ，维度*点的间距=2*8
//    glDrawArrays(GL_TRIANGLES, 0, 3); // 3个点


    // 四边形
//    glUseProgram(program);
//    glEnableVertexAttribArray(vPosition);
//    glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 8, vertexs);
//    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4); // 4个点


    // 图片
    glUseProgram(program);

    // 激活通道 与 使用通道必须一致
    glActiveTexture(GL_TEXTURE5);
    glUniform1i(sampler, 5);// 此处差点错误，导致异常程序


    glBindTexture(GL_TEXTURE_2D, textureId);

    glEnableVertexAttribArray(vPosition);
    glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 8, vertexs);
    glEnableVertexAttribArray(fPosition);
    glVertexAttribPointer(fPosition, 2, GL_FLOAT, false, 8, fragments);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    glBindTexture(GL_TEXTURE_2D, 0);


//    glDisableVertexAttribArray(vPosition);
};


extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_surfaceCreate(JNIEnv *env, jobject instance,
                                                              jobject surface) {

    // init
    nativeWindow = ANativeWindow_fromSurface(env, surface); // 获取到jni里面的surface
    eglThread = new EGLThread();
    eglThread->setRenderType(OPENGL_RENDER_HANDLE);

    eglThread->callBackOnCreate(callback_SurfaceCrete, eglThread);
    eglThread->callBackOnChange(callback_SurfacChange, eglThread);
    eglThread->callBackOnDraw(callback_SurfaceDraw, eglThread);

    eglThread->onSurfaceCreate(nativeWindow);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_surfaceChange(JNIEnv *env, jobject instance,
                                                              jint width, jint height) {
    if (eglThread != NULL) {
        eglThread->onSurfaceChange(width, height);


        // 测试 notifyRender 是否有效，开启后就draw了2次
        usleep(1000000);
        eglThread->notifyRender();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ycl_es2_10_1native_opengl_NativeOpengl_imgData(JNIEnv *env, jobject instance, jint width,
                                                        jint height, jint length,
                                                        jbyteArray data_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    w = width;
    h = height;
    pixels = malloc(length);
    memcpy(pixels, data, length); // 拷贝数据到pixel 里面，因为data在jni里面有释放，所以新建内存重新存储

    env->ReleaseByteArrayElements(data_, data, 0);
}