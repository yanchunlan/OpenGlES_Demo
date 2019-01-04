package com.opengles.book.es2_0_test2.utils;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

/**
 * author: ycl
 * date: 2019-01-04 11:13
 * desc:
 */
public class EasyGlUtils {
    private static final String TAG = "EasyGlUtils";

    private EasyGlUtils() {
    }

    public static int getVboId(float[] vertexData, float[] fragmentData, FloatBuffer vertexBuffer, FloatBuffer fragmentBuffer) {
        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + fragmentData.length * 4,
                null, GLES20.GL_STATIC_DRAW);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, fragmentData.length * 4, fragmentBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        return vbo[0];
    }

    public static int getFboId(int textureId) {
        int[] fbos = new int[1];
        GLES20.glGenBuffers(1, fbos, 0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbos[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureId, 0);
        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "fbo  glCheckFramebufferStatus error");
        } else {
            Log.e(TAG, "fbo glCheckFramebufferStatus success");
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return fbos[0];
    }


}
