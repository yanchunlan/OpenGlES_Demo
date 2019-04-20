//
// Created by pc on 2019/3/4.
//

#ifndef OPENGLES_DEMO_SHADERUTIL_H
#define OPENGLES_DEMO_SHADERUTIL_H


#include <GLES2/gl2.h>

static int loadShaders(int shaderType, const char *code) {
    int shader = glCreateShader(shaderType);
    if (shader != 0) {
        glShaderSource(shader, 1, &code, 0);
        glCompileShader(shader);
        return shader;
    }
    return -1;
}


static int createProgram(const char *vertex, const char *fragment, GLuint *v_shader, GLuint *f_shader) {
    int vertexShader = loadShaders(GL_VERTEX_SHADER, vertex);
    if (vertexShader == 0) return -1;
    int fragmentShader = loadShaders(GL_FRAGMENT_SHADER, fragment);
    if (fragmentShader == 0) return -1;
    int program = glCreateProgram();
    if (program == 0) return -1;
    glAttachShader(program, vertexShader);
    glAttachShader(program, fragmentShader);
    glLinkProgram(program);

    *v_shader = vertexShader;
    *f_shader = fragmentShader;

    return program;
}


#endif //OPENGLES_DEMO_SHADERUTIL_H
