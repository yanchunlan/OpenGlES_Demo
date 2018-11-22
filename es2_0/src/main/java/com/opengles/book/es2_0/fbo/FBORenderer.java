package com.opengles.book.es2_0.fbo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.opengles.book.es2_0.filter.AFilter;
import com.opengles.book.es2_0.filter.GrayFilter;
import com.opengles.book.es2_0.utils.MatrixUtils;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author:  ycl
 * date:  2018/11/22 13:55
 * desc:
 */
public class FBORenderer implements GLSurfaceView.Renderer {
    private AFilter mAFilter;
    private Bitmap mBitmap;

    private ByteBuffer mBuffer;


    private int[] fFrame = new int[1]; // 帧缓冲
    private int[] fRender = new int[1]; // 对象缓冲 ，需要深度的时候，需要它
    private int[] fTexture = new int[2]; // 纹理对象

    public FBORenderer(Resources resources) {
        mAFilter = new GrayFilter(resources);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //  执行创建program,并编译shader,获取texture,并设置渲染参数
        mAFilter.create();
        // 设置matrix，y轴旋转 ，此处没有wh,所以没有projectMatrix,viewMatrix,mvpMatrix
        mAFilter.setMatrix(MatrixUtils.flip(MatrixUtils.getOriginalMatrix(), false, true));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //离屏渲染 跟屏幕无关，所以不需要设置与屏幕相关的参数
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 不需要调用以前的渲染到texture上面去得代码
        if (mBitmap != null && !mBitmap.isRecycled()) {
            createEnvi();
            userEnvi();
            deleteEnvi();
            mBitmap.recycle();
        }
    }

    // 渲染  fRender[0]存深度 -> fFrame[0]存颜色 -> fTexture[1]存储的都绘制到textures[1]上了
    private void userEnvi() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);

        //为FrameBuffer挂载Texture[1]来存储颜色
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fTexture[1], 0);
        //为FrameBuffer挂载fRender[0]来存储深度
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, fRender[0]);

        //绑定FrameBuffer后的绘制会绘制到textures[1]上了
        GLES20.glViewport(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        mAFilter.setTextureId(fTexture[0]);
        mAFilter.draw();
        // 获取到buffer
        GLES20.glReadPixels(0,0,mBitmap.getWidth(),mBitmap.getHeight(),
                GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,mBuffer);

        // 解绑 FrameBuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    // 删除缓冲的节点
    private void deleteEnvi() {
        GLES20.glDeleteTextures(2, fTexture, 0);
        GLES20.glDeleteRenderbuffers(1, fRender, 0);
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
    }

    // 创建
    private void createEnvi() {
        GLES20.glGenFramebuffers(1, fFrame, 0);


        //  生成RenderBuffer，并为RenderBuffer建立数据存储的格式和渲染对象的尺寸 并将其挂在FrameBuffer上面
        GLES20.glGenRenderbuffers(1, fRender, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, fRender[0]);
        // 设置为深度的Render Buffer，并传入大小
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, // 内部格式
                mBitmap.getWidth(), mBitmap.getHeight());
        // 为FrameBuffer挂载fRender[0]来存储深度
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, fRender[0]);
        // 解绑Render Buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);


        // 纹理绑定与创建
        GLES20.glGenTextures(2, fTexture, 0);
        for (int i = 0; i < fTexture.length; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[i]);

            // 下面2个函数都是一样的意思，但是传入参数不一致GLUtils 的方法是可以传入bitmap作为参数
            // 第一个纹理渲染显示的纹理
            if (i == 0) {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap, 0); // border 是边框
            } else {
                // 第二个纹理渲染离屏，即后台存储
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                        mBitmap.getWidth(), mBitmap.getHeight(), 0, GLES20.GL_RGBA,
                        GLES20.GL_UNSIGNED_BYTE, null);
            }

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
    }
}
