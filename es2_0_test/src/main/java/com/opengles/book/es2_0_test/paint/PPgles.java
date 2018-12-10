package com.opengles.book.es2_0_test.paint;

public interface PPgles {

    void init(int program, int vertexShader, int fragmentShader);

    String getVertexShader();

    String getFragmentShader();

    void draw();
}
