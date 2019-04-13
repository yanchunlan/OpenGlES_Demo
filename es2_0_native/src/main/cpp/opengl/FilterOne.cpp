//
// Created by pc on 2019/4/12.
//

#include "FilterOne.h"



FilterOne::FilterOne() {

}

FilterOne::~FilterOne() {

}

void FilterOne::onCreate() {

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
                           "    gl_FragColor=texture2D(sTexture, ft_Position);\n"
                           "}";

    program = createProgrm(vertex, fragment);
    LOGD("opengl program is %d", program);
    vPosition = glGetAttribLocation(program, "v_Position");//顶点坐标 // 此处差点错误，导致异常程序
    fPosition = glGetAttribLocation(program, "f_Position");//纹理坐标  // 此处差点错误，导致异常程序
    sampler = glGetUniformLocation(program, "sTexture");//2D纹理
    u_matrix = glGetUniformLocation(program, "u_Matrix");//矩阵设置之后，需要初始化，否则显示不出来

    LOGD("opengl vPosition is %d", vPosition);
    LOGD("opengl fPosition is %d", fPosition);
    LOGD("opengl sampler is %d", sampler);

    glGenTextures(1, &textureId);
    glBindTexture(GL_TEXTURE_2D, textureId);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // 环绕效果是 重复 GL_CLAMP_TO_EDGE 代表 单独一个
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // 放大缩小效果是 线性
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glBindTexture(GL_TEXTURE_2D, 0);
}

void FilterOne::onChange(int w, int h) {
    glViewport(0, 0, w, h);
    setMatrix(w, h);
}

void FilterOne::draw() {

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
//    glActiveTexture(GL_TEXTURE5);
//    glUniform1i(sampler, 5);// 此处差点错误，导致异常程序


    glUniformMatrix4fv(u_matrix, 1, false, matrix);

    glBindTexture(GL_TEXTURE_2D, textureId);
    if (pixels != NULL) {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
    }

    glEnableVertexAttribArray(vPosition);
    glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 8, vertexs);
    glEnableVertexAttribArray(fPosition);
    glVertexAttribPointer(fPosition, 2, GL_FLOAT, false, 8, fragments);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    glBindTexture(GL_TEXTURE_2D, 0);


//    glDisableVertexAttribArray(vPosition);
}

// onchange的时候设置一次，在后面设置数据的时候在刷新一次
void FilterOne::setMatrix(int width, int height) {

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

void FilterOne::setPilex(void *data, int width, int height, int length) {
    this->w = width;
    this->h = height;
    this->pixels = data;

    // 矩阵重置
    if (surface_width > 0 && surface_height > 0) {
        setMatrix(surface_width, surface_height);
    }
}
