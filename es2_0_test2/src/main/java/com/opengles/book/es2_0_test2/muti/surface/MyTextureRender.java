package com.opengles.book.es2_0_test2.muti.surface;

import android.content.Context;
import android.opengl.GLES20;

import com.opengles.book.es2_0_test2.R;
import com.opengles.book.es2_0_test2.utils.BufferUtils;
import com.opengles.book.es2_0_test2.utils.EasyGlUtils;
import com.opengles.book.es2_0_test2.utils.ShaderUtils;

import java.nio.FloatBuffer;

/**
 * author:  ycl
 * date:  2019/1/4 14:53
 * desc:  需要顶点，像素位置存储 从fbo里面获取的真实图片的真实的数据
 *       也需要简单的2d 顶点，像素创造的program
 */
public class MyTextureRender {

    private Context context;

    protected float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };
    protected FloatBuffer vertexBuffer;

    protected float[] fragmentData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };
    protected FloatBuffer fragmentBuffer;

    private int program;
    protected int vPosition;
    protected int fPosition;
    private int textureid;
    private int sampler;

    private int vboId;


    public MyTextureRender(Context context) {
        this.context = context;
        initBuffer();
    }

    protected void initBuffer() {
        vertexBuffer = BufferUtils.arr2FloatBuffer(vertexData);
        fragmentBuffer = BufferUtils.arr2FloatBuffer(fragmentData);
    }

    public void onCreate() {
        program = ShaderUtils.createProgram(ShaderUtils.readRawTextFile(context, R.raw.vertex_shader),
                ShaderUtils.readRawTextFile(context, R.raw.fragment_shader));

        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        sampler = GLES20.glGetUniformLocation(program, "sTexture");

        vboId = EasyGlUtils.getVboId(vertexData, fragmentData, vertexBuffer, fragmentBuffer);
    }

    public void onChange(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    public void onDraw(int textureId) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f, 0f, 0f, 1f);

        GLES20.glUseProgram(program);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        // 绘制fbo的纹理
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
                0);
        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexData.length * 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


        onDrawExp();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    protected void onDrawExp() {

    }
}
