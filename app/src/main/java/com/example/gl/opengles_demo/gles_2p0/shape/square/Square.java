package com.example.gl.opengles_demo.gles_2p0.shape.square;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * author: ycl
 * date: 2018-09-25 17:34
 * desc: 正方形（由2个三角形组成）
 */
public class Square {
    private FloatBuffer fbb;
    private ShortBuffer sb;

    static final int COORDS_PER_VERTEX = 3;

    static float squareCoords[] = {
            -0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f,
    };
    // 顺时针顶点索引顺序
    static short drawOrder[] = {
            0, 1, 2, 0, 2, 3
    };

    public Square() {
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        fbb = bb.asFloatBuffer();
        fbb.put(squareCoords);
        fbb.position(0);
        // 初始化顶点索引顺序
        ByteBuffer bb1 = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb1.order(ByteOrder.nativeOrder());
        sb = bb1.asShortBuffer();
        sb.put(drawOrder);
        sb.position(0);
    }
}
