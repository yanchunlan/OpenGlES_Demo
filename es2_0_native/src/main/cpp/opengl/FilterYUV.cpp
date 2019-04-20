//
// Created by Administrator on 2019/4/20 0020.
//

#include "FilterYUV.h"

FilterYUV::FilterYUV() {

}

FilterYUV::~FilterYUV() {

}

void FilterYUV::onCreate() {
    vertex = "attribute vec4 v_Position;\n"
             "attribute vec2 f_Position;\n"
             "varying vec2 ft_Position;\n"
             "uniform mat4 u_Matrix;\n"
             "void main() {\n"
             "    ft_Position = f_Position;\n"
             "    gl_Position = v_Position * u_Matrix;\n"
             "}";

    fragment = "precision mediump float;\n"
               "varying vec2 ft_Position;\n"
               "uniform sampler2D sampler_y;\n"
               "uniform sampler2D sampler_u;\n"
               "uniform sampler2D sampler_v;\n"
               "void main() {\n"
               "   float y,u,v;\n"
               "   y = texture2D(sampler_y,ft_Position).r;\n"
               "   u = texture2D(sampler_u,ft_Position).r - 0.5;\n"
               "   v = texture2D(sampler_v,ft_Position).r - 0.5;\n"
               "\n"
               "   vec3 rgb;\n"
               "   rgb.r = y + 1.403 * v;\n"
               "   rgb.g = y - 0.344 * u - 0.714 * v;\n"
               "   rgb.b = y + 1.770 * u;\n"
               "\n"
               "   gl_FragColor = vec4(rgb,1);\n"
               "};";

    program = createProgram(vertex, fragment, &vShader, &fShader);
    LOGD("FilterYUV onCreate program is %d vShader: %d fShader: %d", program, vShader, fShader);
    vPosition = glGetAttribLocation(program, "v_Position");//顶点坐标 // 此处差点错误，导致异常程序
    fPosition = glGetAttribLocation(program, "f_Position");//纹理坐标  // 此处差点错误，导致异常程序
    sampler_y = glGetUniformLocation(program, "sampler_y");
    sampler_u = glGetUniformLocation(program, "sampler_u");
    sampler_v = glGetUniformLocation(program, "sampler_v");
    u_matrix = glGetUniformLocation(program, "u_Matrix");//矩阵设置之后，需要初始化，否则显示不出来

    LOGD("FilterYUV onCreate vPosition is %d", vPosition);
    LOGD("FilterYUV onCreate fPosition is %d", fPosition);
    LOGD("FilterYUV onCreate sampler_y is %d", sampler_y);
    LOGD("FilterYUV onCreate sampler_u is %d", sampler_u);
    LOGD("FilterYUV onCreate sampler_v is %d", sampler_v);
    LOGD("FilterYUV onCreate u_matrix is %d", u_matrix);

    glGenTextures(3, textureIds);
    for (int i = 0; i < 3; ++i) {
        glBindTexture(GL_TEXTURE_2D, textureIds[i]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
                        GL_REPEAT); // 环绕效果是 重复 GL_CLAMP_TO_EDGE 代表 单独一个
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // 放大缩小效果是 线性
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}

void FilterYUV::onChange(int w, int h) {
//    this->surface_width = w;
//    this->surface_height = h;
    glViewport(0, 0, w, h);
    setMatrix(w, h);
}

void FilterYUV::draw() {
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    glUseProgram(program);
    glUniformMatrix4fv(u_matrix, 1, false, matrix);

    glEnableVertexAttribArray(vPosition);
    glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 8, vertexs);
    glEnableVertexAttribArray(fPosition);
    glVertexAttribPointer(fPosition, 2, GL_FLOAT, false, 8, fragments);

    // 注意yuv ，glTexImage2D 中不是设置GL_RGBA类型了，是 GL_LUMINANCE 类型
    if (yuv_w > 0 && yuv_h > 0) {
        if (y != NULL) {
            glActiveTexture(GL_TEXTURE0);
            glUniform1i(sampler_y, 0);
            glBindTexture(GL_TEXTURE_2D, textureIds[0]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, yuv_w, yuv_h, 0, GL_LUMINANCE,
                         GL_UNSIGNED_BYTE, y);
        }
        if (u != NULL) {
            glActiveTexture(GL_TEXTURE1);
            glUniform1i(sampler_u, 1);
            glBindTexture(GL_TEXTURE_2D, textureIds[1]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, yuv_w / 2, yuv_h / 2, 0, GL_LUMINANCE,
                         GL_UNSIGNED_BYTE, u);
        }
        if (v != NULL) {
            glActiveTexture(GL_TEXTURE2);
            glUniform1i(sampler_v, 2);
            glBindTexture(GL_TEXTURE_2D, textureIds[2]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, yuv_w / 2, yuv_h / 2, 0, GL_LUMINANCE,
                         GL_UNSIGNED_BYTE, v);
        }

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}

void FilterYUV::setMatrix(int width, int height) {
    initMatrix(matrix);

    // 正交投影
    float screen_r = 1.0 * width / height;
    float picture_r = 1.0 * yuv_w / yuv_h;

    if (screen_r > picture_r) {//图片宽度缩放，则是按照高度最小去适配

        float realWidth = 1.0 * height / yuv_h * yuv_w;
        float r = width / realWidth;  // 宽度缩放的值
        orthoM(-r, r, -1, 1, matrix);
    } else { // 图片高度缩放，则是按照宽度最小去适配

        float realHeight = 1.0 * width / yuv_w * yuv_h;
        float r = height / realHeight;  // 高度缩放的值
        orthoM(-1, 1, -r, r, matrix);
    }

}

void FilterYUV::setYuvData(void *y, void *u, void *v, int width, int height) {

    LOGE("FilterYUV::setYuvData  width %d height %d surface_width %d surface_height %d", width,
         height, surface_width, surface_height)

    // 先释放，再重置
    if (width > 0 && height > 0) {

        // 宽高只要其中之一有变化，就需要重新设置yuv的大小和重置矩阵
        if (yuv_w != width || yuv_h != height) {
            yuv_w = width;
            yuv_h = height;
            if (y != NULL) {
                free(y);
                y = NULL;
            }
            if (u != NULL) {
                free(u);
                u = NULL;
            }
            if (v != NULL) {
                free(v);
                v = NULL;
            }
            y = malloc(yuv_w * yuv_h);
            u = malloc(yuv_w * yuv_h / 4);
            v = malloc(yuv_w * yuv_h / 4);
            if (surface_width > 0 && surface_height > 0) {
                setMatrix(surface_width, surface_height);
            }
        }
        // copy数据到内存上面
        memcpy(this->y, y, yuv_w * yuv_h);
        memcpy(this->u, u, yuv_w * yuv_h / 4);
        memcpy(this->v, v, yuv_w * yuv_h / 4);
    }
}


void FilterYUV::destroy() {
    LOGE("FilterYUV::destroy")
    glDeleteTextures(3, textureIds);
    glDetachShader(program, vShader);
    glDetachShader(program, fShader);
    glDeleteShader(vShader);
    glDeleteShader(fShader);
    glDeleteProgram(program);
}

void FilterYUV::destroySource() {
    LOGE("FilterYUV::destroySource")
    yuv_w = 0;
    yuv_h = 0;
    if (y != NULL) {
        free(y);
        y = NULL;
    }
    if (u != NULL) {
        free(u);
        u = NULL;
    }
    if (v != NULL) {
        free(v);
        v = NULL;
    }
}