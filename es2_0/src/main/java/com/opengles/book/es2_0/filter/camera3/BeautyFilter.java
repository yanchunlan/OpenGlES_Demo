package com.opengles.book.es2_0.filter.camera3;

import android.content.res.Resources;
import android.opengl.GLES20;

import com.opengles.book.es2_0.filter.AFilter;

/**
 * author:  ycl
 * date:  2018/12/6 9:42
 * desc:
 */
public class BeautyFilter extends AFilter {
    private int gHiternum; // 便利数量
    private int gHaaCoef;  //参数
    private int gHmixCoef;//混合系数

    private int gHWidth;
    private int gHHeight;


    // params
    private int iternum;
    private float aaCoef;
    private float mixCoef;

    private int mWidth = 720;
    private int mHeight = 1280;


    public BeautyFilter(Resources mRes) {
        super(mRes);
        setFlag(0);
    }

    private void a(int a, float b, float c) {
        this.iternum = a;
        this.aaCoef = b;
        this.mixCoef = c;
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/beauty/beauty.vert", "shader/beauty/beauty.frag");
        gHaaCoef = GLES20.glGetUniformLocation(mProgram, "aaCoef");
        gHmixCoef = GLES20.glGetUniformLocation(mProgram, "mixCoef");
        gHiternum = GLES20.glGetUniformLocation(mProgram, "iternum");
        gHWidth = GLES20.glGetUniformLocation(mProgram, "mWidth");
        gHHeight = GLES20.glGetUniformLocation(mProgram, "mHeight");
    }

    // 美颜一直都是只有几个级别，seekBar调整的一直都是面具的intensity
    @Override
    public void setFlag(int flag) {
        super.setFlag(flag);
        switch (flag) {
            case 1:
                a(1, 0.19f, 0.54f);
                break;
            case 2:
                a(2, 0.29f, 0.54f);
                break;
            case 3:
                a(3, 0.17f, 0.39f);
                break;
            case 4:
                a(3, 0.25f, 0.54f);
                break;
            case 5:
                a(4, 0.13f, 0.54f);
                break;
            case 6:
                a(4, 0.19f, 0.69f);
                break;
            default:
                a(0, 0f, 0f);
                break;
        }
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        GLES20.glUniform1f(gHiternum, iternum);
        GLES20.glUniform1f(gHaaCoef, aaCoef);
        GLES20.glUniform1f(gHmixCoef, mixCoef);

        GLES20.glUniform1f(gHWidth, mWidth);
        GLES20.glUniform1f(gHHeight, mHeight);
    }
}
