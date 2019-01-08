package com.opengles.book.es2_0_test2.yuv;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.opengles.book.es2_0_test2.R;
import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;
import com.opengles.book.es2_0_test2.muti.surface.MyTextureRender;
import com.opengles.book.es2_0_test2.utils.BufferUtils;
import com.opengles.book.es2_0_test2.utils.EasyGlUtils;
import com.opengles.book.es2_0_test2.utils.MatrixUtils;
import com.opengles.book.es2_0_test2.utils.ShaderUtils;
import com.opengles.book.es2_0_test2.utils.TextureUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * author:  ycl
 * date:  2019/1/8 11:02
 * desc:
 *        传入yuv数据，传入之后会在shader里面转换成rgb,并转换成rgb像素数据并显示
 */
public class YuvRender implements EglSurfaceView.EglRenderer {
    private Context context;
    private float[] vertexData = {
            1f,1f,
            -1f,1f,
            1f,-1f,
            -1f,-1f
    };
    private FloatBuffer vertexBuffer;

    private float[] fragmentData = {
            1f,0f,
            0f,0f,
            1f,1f,
            0f,1f
    };
    private FloatBuffer fragmentBuffer;


    private int program;
    private int vPosition;
    private int fPosition;

    private int sampler_y;
    private int sampler_u;
    private int sampler_v;

    private int[] texture_yuv;

    private int fboId;
    private int vboId;
    private int textureid; // fboTextureId


    private int w;
    private int h;

    private Buffer y;
    private Buffer u;
    private Buffer v;

    private float[] matrix = new float[16];
    private int u_matrix;

    private MyTextureRender mTextureRender;


    public YuvRender(Context context) {
        this.context = context;
        mTextureRender = new MyTextureRender(context);

        vertexBuffer = BufferUtils.arr2FloatBuffer(vertexData);
        fragmentBuffer = BufferUtils.arr2FloatBuffer(fragmentData);

        Matrix.setIdentityM(matrix, 0); // 初始化matrix ，并归零
    }

    @Override
    public void onSurfaceCreated() {
        mTextureRender.onCreate();

        program = ShaderUtils.createProgram(ShaderUtils.readRawTextFile(context, R.raw.vertex_shader_m ),
                ShaderUtils.readRawTextFile(context, R.raw.fragment_shader_yuv ));

        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        u_matrix = GLES20.glGetUniformLocation(program, "u_Matrix");

        sampler_y = GLES20.glGetUniformLocation(program, "sampler_y");
        sampler_u = GLES20.glGetUniformLocation(program, "sampler_u");
        sampler_v = GLES20.glGetUniformLocation(program, "sampler_v");

        vboId = EasyGlUtils.getVboId(vertexData, fragmentData, vertexBuffer, fragmentBuffer);

        // yuv纹理 只设置3个纹理，但是没有绑定数据，宽高
        texture_yuv = TextureUtils.genTexturesWithParameter(3, 0);

        // 缓冲区
        textureid = TextureUtils.genTexturesWithParameter(1, 0, GLES20.GL_RGBA, 640, 360)[0];
        fboId = EasyGlUtils.getFboId(textureid);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        // 用了fbo ,纹理需要x旋转180
        Matrix.rotateM(matrix, 0, 180f, 1, 0, 0);

        GLES20.glViewport(0, 0, width, height);
        mTextureRender.onChange(width, height);
    }

    @Override
    public void onDrawFrame() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f, 0f, 0f, 1f);

        if (w > 0 && h > 0 && y != null && u != null && v != null) {
            GLES20.glUseProgram(program);
            GLES20.glUniformMatrix4fv(u_matrix, 1, false, matrix, 0);

            // y:u:v=1:4:4 ,宽高缩半
            // y
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture_yuv[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w, h,
                    0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, y);
            GLES20.glUniform1i(sampler_y, 0);
            // u
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture_yuv[1]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w / 2, h / 2,
                    0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, u);
            GLES20.glUniform1i(sampler_u, 1);
            // v
            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture_yuv[2]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w / 2, h / 2,
                    0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, v);
            GLES20.glUniform1i(sampler_v, 2);

            y.clear();
            u.clear();
            v.clear();

            y = null;
            u = null;
            v = null;


            // 顶点缓冲
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);

            GLES20.glEnableVertexAttribArray(vPosition);
            GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
                    0);
            GLES20.glEnableVertexAttribArray(fPosition);
            GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                    vertexData.length * 4);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            // 资源释放
            GLES20.glDisableVertexAttribArray(vPosition);
            GLES20.glDisableVertexAttribArray(fPosition);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); // 解绑texture
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        // 真实图片绘制
        mTextureRender.onDraw(textureid);
    }

    public void setFrameData(int width, int height, byte[] by, byte[] bu, byte[] bv) {
        this.w = width;
        this.h = height;
        this.y = ByteBuffer.wrap(by);
        this.u = ByteBuffer.wrap(bu);
        this.v = ByteBuffer.wrap(bv);
    }
}
