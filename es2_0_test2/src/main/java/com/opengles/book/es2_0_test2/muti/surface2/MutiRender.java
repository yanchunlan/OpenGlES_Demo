package com.opengles.book.es2_0_test2.muti.surface2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import com.opengles.book.es2_0_test2.R;
import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;
import com.opengles.book.es2_0_test2.utils.BufferUtils;
import com.opengles.book.es2_0_test2.utils.EasyGlUtils;
import com.opengles.book.es2_0_test2.utils.ShaderUtils;
import com.opengles.book.es2_0_test2.utils.TextureUtils;

import java.nio.FloatBuffer;

/**
 * author: ycl
 * date: 2019-01-04 10:57
 * desc:
 */
public class MutiRender implements EglSurfaceView.EglRenderer {
    private Context context;
    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,

            -0.25f, -0.25f,
            0.25f, -0.25f,
            0f, 0.15f

    };
    private FloatBuffer vertexBuffer;

    private float[] fragmentData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f

    };
    private FloatBuffer fragmentBuffer;


    private int program;
    private int vPosition;
    private int fPosition;
    private int sampler;


    private int vboId;

    private int textureId;
    private int migTextureId;

    private int index;


    public MutiRender(Context context) {
        this.context = context;
        vertexBuffer = BufferUtils.arr2FloatBuffer(vertexData);
        fragmentBuffer = BufferUtils.arr2FloatBuffer(fragmentData);
    }


    public void setTextureId(int textureId, int index) {
        this.textureId = textureId;
        this.index = index;
    }


    @Override
    public void onSurfaceCreated() {
        String fragmentSource;
        if (index == 0) {
            fragmentSource = ShaderUtils.readRawTextFile(context, R.raw.fragment_shader1);
        } else if (index == 1) {
            fragmentSource = ShaderUtils.readRawTextFile(context, R.raw.fragment_shader2);
        } else if (index == 2) {
            fragmentSource = ShaderUtils.readRawTextFile(context, R.raw.fragment_shader3);
        } else {
            fragmentSource = ShaderUtils.readRawTextFile(context, R.raw.fragment_shader);
        }


        program = ShaderUtils.createProgram(ShaderUtils.readRawTextFile(context, R.raw.vertex_shader), fragmentSource);

        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        sampler = GLES20.glGetUniformLocation(program, "sTexture");

        vboId = EasyGlUtils.getVboId(vertexData, fragmentData, vertexBuffer, fragmentBuffer);

        // 最小空间的图片
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.muti);
        migTextureId = TextureUtils.genTexturesWithParameter(1, 0, b)[0];
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f, 0f, 0f, 1f);



        GLES20.glUseProgram(program);

        // 顶点缓冲
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);

        //绘制第一张图片
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
                0);
        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexData.length * 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        //绘制第二张图片
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, migTextureId);
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
                8 * 4);
        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexData.length * 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3); // 3个点


        GLES20.glDisableVertexAttribArray(vPosition);
        GLES20.glDisableVertexAttribArray(fPosition);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); // 解绑texture

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
}
