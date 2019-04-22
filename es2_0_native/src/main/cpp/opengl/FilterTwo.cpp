//
// Created by Administrator on 2019/4/20 0020.
//

#include "FilterTwo.h"

FilterTwo::FilterTwo() {

}

FilterTwo::~FilterTwo() {

}

void FilterTwo::onCreate() {
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
               "uniform sampler2D sTexture;\n"
               "void main() {\n"
               "    lowp vec4 textureColor = texture2D(sTexture, ft_Position);\n"
               "    float gray = textureColor.r * 0.2125 + textureColor.g * 0.7154 + textureColor.b * 0.0721;\n"
               "    gl_FragColor = vec4(gray,gray,gray,textureColor.w);\n"
               "}";

    program = createProgram(vertex, fragment, &vShader, &fShader);
    LOGD("FilterTwo onCreate program is %d vShader: %d fShader: %d", program, vShader, fShader);
    vPosition = glGetAttribLocation(program, "v_Position");//顶点坐标 // 此处差点错误，导致异常程序
    fPosition = glGetAttribLocation(program, "f_Position");//纹理坐标  // 此处差点错误，导致异常程序
    sampler = glGetUniformLocation(program, "sTexture");//2D纹理
    u_matrix = glGetUniformLocation(program, "u_Matrix");//矩阵设置之后，需要初始化，否则显示不出来

    LOGD("FilterTwo onCreate vPosition is %d", vPosition);
    LOGD("FilterTwo onCreate fPosition is %d", fPosition);
    LOGD("FilterTwo onCreate sampler is %d", sampler);
    LOGD("FilterTwo onCreate u_matrix is %d", u_matrix);

    glGenTextures(1, &textureId);
    glBindTexture(GL_TEXTURE_2D, textureId);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
                    GL_REPEAT); // 环绕效果是 重复 GL_CLAMP_TO_EDGE 代表 单独一个
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // 放大缩小效果是 线性
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glBindTexture(GL_TEXTURE_2D, 0);
}

void FilterTwo::onChange(int w, int h) {
//    this->surface_width = w;
//    this->surface_height = h;
    glViewport(0, 0, w, h);
    setMatrix(w, h);
}

void FilterTwo::draw() {
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    glUseProgram(program);
    glUniformMatrix4fv(u_matrix, 1, GL_FALSE, matrix);

    // 激活通道 与 使用通道必须一致
    glActiveTexture(GL_TEXTURE5);
    glUniform1i(sampler, 5);// 此处差点错误，导致异常程序

    glBindTexture(GL_TEXTURE_2D, textureId);
    if (pixels != NULL) {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
    }

    glEnableVertexAttribArray(vPosition);
    glVertexAttribPointer(vPosition, 2, GL_FLOAT, GL_FALSE, 8, vertexs);
    glEnableVertexAttribArray(fPosition);
    glVertexAttribPointer(fPosition, 2, GL_FLOAT, GL_FALSE, 8, fragments);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    glBindTexture(GL_TEXTURE_2D, 0);
}


void FilterTwo::setMatrix(int width, int height) {
    initMatrix(matrix);

    // 正交投影
    float screen_r = 1.0 * width / height;
    float picture_r = 1.0 * w / h;

    if (screen_r > picture_r) {//图片宽度缩放，则是按照高度最小去适配

        float realWidth = 1.0 * height / h * w;
        float r = width / realWidth;  // 宽度缩放的值
        orthoM(-r, r, -1, 1, matrix);
    } else { // 图片高度缩放，则是按照宽度最小去适配

        float realHeight = 1.0 * width / w * h;
        float r = height / realHeight;  // 高度缩放的值
        orthoM(-1, 1, -r, r, matrix);
    }
}

void FilterTwo::setPilex(void *data, int width, int height, int length) {
    this->w = width;
    this->h = height;
    this->pixels = data;

    LOGE("FilterTwo::setPilex  width %d height %d surface_width %d surface_height %d", width,
         height, surface_width, surface_height)

    // 当重置图片的时候，如果有矩阵重置
    // 可能存在一种情况是线程还未创建，就执行setPilex,surfaceWH都为0，就不会重置矩阵，即使矩阵的重置也是需要有纹理，控件宽高的
    if (surface_width > 0 && surface_height > 0) {
        setMatrix(surface_width, surface_height);
    }
}


void FilterTwo::destroy() {
    LOGE("FilterTwo::destroy")
    glDeleteTextures(1, &textureId);
    glDetachShader(program, vShader);
    glDetachShader(program, fShader);
    glDeleteShader(vShader);
    glDeleteShader(fShader);
    glDeleteProgram(program);
}

void FilterTwo::destroySource() {
    LOGE("FilterTwo::destroySource")
    if (pixels != NULL) {
        pixels = NULL;
    }
}