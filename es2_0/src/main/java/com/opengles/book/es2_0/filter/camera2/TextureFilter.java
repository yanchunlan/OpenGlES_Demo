package com.opengles.book.es2_0.filter.camera2;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import com.opengles.book.es2_0.filter.AFilter;
import com.opengles.book.es2_0.utils.EasyGlUtils;

import java.nio.ByteBuffer;

/**
 * author:  ycl
 * date:  2018/11/29 17:31
 * desc:
 *      修复bug记录：
 *      onDraw 不需要绘制父类,且方法错误，是draw
 *      getOutputTexture 实现类必须实现
 */
public class TextureFilter extends AFilter {
    private CameraFilter mFilter;

    private int width = 0;
    private int height = 0;

    private int[] fFreame = new int[1];
    private int[] fTexture = new int[1];
    private int[] fCameraTexture = new int[1];

    // surfaceTexture
    private SurfaceTexture mSurfaceTexture;
    private float[] CoordOM = new float[16];


    public TextureFilter(Resources mRes) {
        super(mRes);
        mFilter = new CameraFilter(mRes);
    }

    @Override
    protected void initBuffer() {


    }

    @Override
    public void setFlag(int flag) {
        mFilter.setFlag(flag);
    }

    @Override
    public void setMatrix(float[] matrix) {
        mFilter.setMatrix(matrix);
    }

    @Override
    public int getOutputTexture() {
        return fTexture[0];
    }

    public SurfaceTexture getTexture() {
        return mSurfaceTexture;
    }

    @Override
    protected void onCreate() {
        mFilter.create();

        //  当前界面有2 个texture, 一个用于缓冲，一个用于界面显示
        GLES20.glGenTextures(1, fCameraTexture, 0);
        mSurfaceTexture = new SurfaceTexture(fCameraTexture[0]);
    }


    @Override
    protected void onSizeChanged(int w, int h) {
        mFilter.setSize(w, h);
        if (width != w && height != h) {
            width = w;
            height = h;

            deleteFrameBuffer();
            // 放入到缓存区
            GLES20.glGenFramebuffers(1, fFreame, 0);
            EasyGlUtils.genTexturesWithParameter(1, fTexture, 0,
                    GLES20.GL_RGBA, w, h);
        }
    }

    @Override
    public void draw() {
        boolean isDepthTest = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        if (isDepthTest) {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }

        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();

            // 把矩阵设置给AFilter
            mSurfaceTexture.getTransformMatrix(CoordOM);
            mFilter.setCoordMatrix(CoordOM);
        }

        // 从缓冲区取数据
        EasyGlUtils.bindFrameTexture(fFreame[0], fTexture[0]);
        GLES20.glViewport(0, 0, width, height);
        // 后续会绑定到texture上面
        mFilter.setTextureId(fCameraTexture[0]);
        mFilter.draw();
        EasyGlUtils.unBindFrameBuffer();


        if (isDepthTest) {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, fFreame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
    }
}
