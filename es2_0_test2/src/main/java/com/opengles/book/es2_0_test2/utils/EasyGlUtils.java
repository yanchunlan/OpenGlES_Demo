package com.opengles.book.es2_0_test2.utils;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

/**
 * author: ycl
 * date: 2019-01-04 11:13
 * desc:
 */
public class EasyGlUtils {

    private EasyGlUtils() {
    }


    public static int getVboId(float[] vertexData, float[] fragmentData, FloatBuffer vertexBuffer, FloatBuffer fragmentBuffer) {
        int[] vboId = new int[1];
        GLES20.glGenBuffers(1, vboId, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + fragmentData.length * 4,
                null, GLES20.GL_STATIC_DRAW);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, fragmentData.length * 4, fragmentBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        return vboId[0];
    }
}
