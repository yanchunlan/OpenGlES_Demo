package com.opengles.book.es2_0.vary;

import android.content.res.Resources;
import android.opengl.GLES20;

import com.opengles.book.es2_0.utils.BufferUtils;
import com.opengles.book.es2_0.utils.ShaderUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @Author: ycl
 * @Date: 2018/11/9 11:11
 * @Desc:
 */
public class Cube {

    final float cubePositions[] = {
            -1.0f, 1.0f, 1.0f,    //正面左上0
            -1.0f, -1.0f, 1.0f,   //正面左下1
            1.0f, -1.0f, 1.0f,    //正面右下2
            1.0f, 1.0f, 1.0f,     //正面右上3
            -1.0f, 1.0f, -1.0f,    //反面左上4
            -1.0f, -1.0f, -1.0f,   //反面左下5
            1.0f, -1.0f, -1.0f,    //反面右下6
            1.0f, 1.0f, -1.0f,     //反面右上7
    };
    final short index[] = {
            6, 7, 4, 6, 4, 5,    //后面
            6, 3, 7, 6, 2, 3,    //右面
            6, 5, 1, 6, 1, 2,    //下面
            0, 3, 2, 0, 2, 1,    //正面
            0, 1, 5, 0, 5, 4,    //左面
            0, 7, 3, 0, 4, 7,    //上面


    };

    float color[] = {
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
    };
    private FloatBuffer vertexBuf, colorBuf;
    private ShortBuffer indexBuf;
    private Resources res;
    private int mProgram;
    private int hVertex;
    private int hColor;
    private int hMatrix;
    private float[] mMatrix;

    public Cube(Resources res) {
        this.res = res;
        initData();
    }

    private void initData() {
        vertexBuf = BufferUtils.arr2FloatBuffer(cubePositions);
        colorBuf = BufferUtils.arr2FloatBuffer(color);
        indexBuf = BufferUtils.arr2ShortBuffer(index);
    }

    public void create() {
        mProgram = ShaderUtils.createProgram(res, "vary/vertex.sh", "vary/fragment.sh");
        hVertex = GLES20.glGetAttribLocation(mProgram, "vPosition");
        hColor = GLES20.glGetAttribLocation(mProgram, "aColor");
        hMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
    }

    public void setMatrix(float[] matrix) {
        mMatrix = matrix;
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        if (mMatrix != null) {
            GLES20.glUniformMatrix4fv(hMatrix, 1, false, mMatrix, 0);
        }
        GLES20.glEnableVertexAttribArray(hVertex);
        GLES20.glVertexAttribPointer(hVertex, 3, GLES20.GL_FLOAT, false, 0, vertexBuf);

        GLES20.glEnableVertexAttribArray(hColor);
        GLES20.glVertexAttribPointer(hColor, 4, GLES20.GL_FLOAT, false, 0, colorBuf);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length,GLES20.GL_UNSIGNED_SHORT,indexBuf);

        GLES20.glDisableVertexAttribArray(hVertex);
        GLES20.glDisableVertexAttribArray(hColor);
    }
}
