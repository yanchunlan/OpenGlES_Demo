package com.opengles.book.es2_0.filter.camera2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.opengles.book.es2_0.utils.EasyGlUtils;
import com.opengles.book.es2_0.utils.MatrixUtils;

/**
 * author: ycl
 * date: 2018-12-12 21:12
 * desc: 水印Filter
 */
public class WaterMarkFilter extends NoFilter {
    private Bitmap mBitmap;
    private NoFilter mFilter;
    private int width, height;
    private int x, y, w, h;

    public WaterMarkFilter(Resources mRes) {
        super(mRes);
        mFilter = new NoFilter(mRes);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mFilter.create();
        int textureId = EasyGlUtils.genTexturesWithParameter(1, 0, mBitmap)[0];
        MatrixUtils.flip(mFilter.getMatrix(), false, true);
        mFilter.setTextureId(textureId);
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        this.width = width;
        this.height = height;
        mFilter.setSize(width, height);
    }

    @Override
    protected void onDraw() {
        super.onDraw();
        // 绘制水印宽高，所以设置为DataSize的宽高
        GLES20.glViewport(x, y, w == 0 ? mBitmap.getWidth() : w, h == 0 ? mBitmap.getHeight() : h);


        // 水印不开启深度测试
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        // 开启混合，并设置方程式src ->  GL_SRC_COLOR  ,des -> GL_DST_ALPHA
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_COLOR, GLES20.GL_DST_ALPHA);
        mFilter.draw();
        GLES20.glDisable(GLES20.GL_BLEND);


        GLES20.glViewport(0, 0, width, height);
    }

    public void setBitmap(Bitmap bitmap) {
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        mBitmap = bitmap;
    }

    public void setPosition(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
}
