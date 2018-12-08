package com.opengles.book.es2_0.filter.camera3;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.opengles.book.es2_0.filter.AFilter;
import com.opengles.book.es2_0.utils.EasyGlUtils;

import java.io.IOException;

/**
 * author:  ycl
 * date:  2018/12/6 9:41
 * desc:  面具图片
 */
public class LookupFilter extends AFilter {

    private int mHIntensity;
    private int mHMaskTexture;

    private float intensity; // 强度

    private int[] mastTextures = new int[1];
    private Bitmap mBitmap;

    public LookupFilter(Resources mRes) {
        super(mRes);
    }


    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void setMaskImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public void setMaskImageAssets(String fileName) {
        try {
            mBitmap = BitmapFactory.decodeStream(mRes.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("lookup/lookup.vert", "lookup/lookup.frag");
        mHMaskTexture = GLES20.glGetUniformLocation(mProgram, "maskTexture");
        mHIntensity = GLES20.glGetUniformLocation(mProgram, "intensity");
        EasyGlUtils.genTexturesWithParameter(1, mastTextures, 0, GLES20.GL_RGBA, 512, 512);
//        EasyGlUtils.genTexturesWithParameter(1,mastTextures,0, mBitmap);
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }


    // 矩阵设置
    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();

        GLES20.glUniform1f(mHIntensity, intensity);

        if (mastTextures[0] != 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + getTextureType() + 1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mastTextures[0]);
            // 设置bitmap到里面去
            if (mBitmap != null && !mBitmap.isRecycled()) {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
                mBitmap.recycle();
            }
            GLES20.glUniform1i(mHMaskTexture, getTextureType() + 1);
        }
    }
}
