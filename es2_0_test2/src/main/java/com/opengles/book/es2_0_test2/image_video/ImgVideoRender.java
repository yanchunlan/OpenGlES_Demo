package com.opengles.book.es2_0_test2.image_video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

import com.opengles.book.es2_0_test2.R;
import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;
import com.opengles.book.es2_0_test2.muti.surface.MyRender;
import com.opengles.book.es2_0_test2.muti.surface.MyTextureRender;
import com.opengles.book.es2_0_test2.utils.BufferUtils;
import com.opengles.book.es2_0_test2.utils.EasyGlUtils;
import com.opengles.book.es2_0_test2.utils.ShaderUtils;
import com.opengles.book.es2_0_test2.utils.TextureUtils;

import java.nio.FloatBuffer;

/**
 * author:  ycl
 * date:  2019/1/7 17:42
 * desc:
 */
public class ImgVideoRender implements EglSurfaceView.EglRenderer {
    private static final String TAG = "ImgVideoRender";
    private Context context;
    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,
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
    private int textureid;


    private int vboId;
    private int fboId;

    private int imgTextureId;

    private int srcImg = 0;


    private MyTextureRender mTextureRender;
    private MyRender.OnRenderCreateListener onRenderCreateListener;


    public ImgVideoRender(Context context) {
        this.context = context;
        mTextureRender = new MyTextureRender(context);

        vertexBuffer = BufferUtils.arr2FloatBuffer(vertexData);
        fragmentBuffer = BufferUtils.arr2FloatBuffer(fragmentData);
    }

    @Override
    public void onSurfaceCreated() {
        mTextureRender.onCreate();

        program = ShaderUtils.createProgram(ShaderUtils.readRawTextFile(context, R.raw.vertex_shader),
                ShaderUtils.readRawTextFile(context, R.raw.fragment_shader));

        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");

        vboId = EasyGlUtils.getVboId(vertexData, fragmentData, vertexBuffer, fragmentBuffer);
        // 缓冲区
        textureid = TextureUtils.genTexturesWithParameter(1, 0, GLES20.GL_RGBA, 720, 1280)[0];

        // 激活一个通道，绘制图片即可
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        GLES20.glUniform1i(sampler, 0);


        fboId = EasyGlUtils.getFboId(textureid);

        if (onRenderCreateListener != null) {
            onRenderCreateListener.onCreate(textureid);
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mTextureRender.onChange(width, height);
    }

    @Override
    public void onDrawFrame() {

        Bitmap b = BitmapFactory.decodeResource(context.getResources(), srcImg);
        imgTextureId = TextureUtils.genTexturesWithParameter(1, 0, b)[0];
        if (b != null && !b.isRecycled()) {
            b.recycle();
            b = null;
        }

        Log.d(TAG, "id is : " + imgTextureId);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f, 0f, 0f, 1f);

        GLES20.glUseProgram(program);

        // 顶点缓冲
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);

        //绘制第一张图片
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imgTextureId);

        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
                0);
        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexData.length * 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


        GLES20.glDisableVertexAttribArray(vPosition);
        GLES20.glDisableVertexAttribArray(fPosition);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); // 解绑texture

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        // 不断创建texture，用完就不断删除它
        int[] ids = new int[]{imgTextureId};
        GLES20.glDeleteTextures(1, ids, 0);


        // 真实图片绘制
        mTextureRender.onDraw(textureid);
    }


    public void setCurrentSrcImg(int srcImg) {
        this.srcImg = srcImg;
    }

    public void setOnRenderCreateListener(MyRender.OnRenderCreateListener onRenderCreateListener) {
        this.onRenderCreateListener = onRenderCreateListener;
    }

    public interface OnRenderCreateListener {
        void onCreate(int textureId);
    }
}
