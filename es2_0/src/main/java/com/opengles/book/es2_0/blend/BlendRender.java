package com.opengles.book.es2_0.blend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.opengles.book.es2_0.R;
import com.opengles.book.es2_0.filter.camera2.NoFilter;
import com.opengles.book.es2_0.utils.EasyGlUtils;
import com.opengles.book.es2_0.utils.MatrixUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-12-05 22:45
 * desc:
 */
public class BlendRender implements GLSurfaceView.Renderer {

    // -------------- 函数 glBlendFunc及glBlendFuncSeparate
    private String[] paramStr = new String[]{
            "GL_ZERO", "GL_ONE", "GL_SRC_COLOR", "GL_ONE_MINUS_SRC_COLOR",
            "GL_DST_COLOR", "GL_ONE_MINUS_DST_COLOR", "GL_SRC_ALPHA", "GL_ONE_MINUS_SRC_ALPHA",
            "GL_DST_ALPHA", "GL_ONE_MINUS_DST_ALPHA", "GL_CONSTANT_COLOR", "GL_ONE_MINUS_CONSTANT_COLOR",
            "GL_CONSTANT_ALPHA", "GL_ONE_MINUS_CONSTANT_ALPHA", "GL_SRC_ALPHA_SATURATE"
    };

    private int[] paramInt = new int[]{
            GLES20.GL_ZERO, GLES20.GL_ONE, GLES20.GL_SRC_COLOR, GLES20.GL_ONE_MINUS_SRC_COLOR,
            GLES20.GL_DST_COLOR, GLES20.GL_ONE_MINUS_DST_COLOR, GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA,
            GLES20.GL_DST_ALPHA, GLES20.GL_ONE_MINUS_DST_ALPHA, GLES20.GL_CONSTANT_COLOR, GLES20.GL_ONE_MINUS_CONSTANT_COLOR,
            GLES20.GL_CONSTANT_ALPHA, GLES20.GL_ONE_MINUS_CONSTANT_ALPHA, GLES20.GL_SRC_ALPHA_SATURATE
    };

    // ---------- 方程式 glBlendEquation及glBlendEquationSeparate
    private String[] equaStr = new String[]{
            "GL_FUNC_ADD", "GL_FUNC_SUBTRACT", "GL_FUNC_REVERSE_SUBTRACT"
    };

    private int[] equaInt = new int[]{
            GLES20.GL_FUNC_ADD, GLES20.GL_FUNC_SUBTRACT, GLES20.GL_FUNC_REVERSE_SUBTRACT
    };
    //------------------------------------------------------------------------------

    private NoFilter mDstFilter;
    private NoFilter mSrcFilter;

    private Bitmap srcBitmap;
    private Bitmap dstBitmap;

    private int width, height;

    private int nSrcPar = paramInt[2];
    private int nDstPar = paramInt[7];
    private int nEquaIndex = 0;  // 方程式下标

    public BlendRender(Context context) {
        mSrcFilter = new NoFilter(context.getResources());
        mDstFilter = new NoFilter(context.getResources());
        dstBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.blend_bg);
        srcBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.blend_src);
    }


    public String equaStr() {
        return equaStr[nEquaIndex];
    }

    public void addEquaStr() {
        nEquaIndex++;
        if (nEquaIndex >= 3) {
            nEquaIndex = 0;
        }
    }

    public String[] getParamStr() {
        return paramStr;
    }

    public int[] getParamInt() {
        return paramInt;
    }

    public int getnSrcPar() {
        return nSrcPar;
    }

    public void setnSrcPar(int nSrcPar) {
        this.nSrcPar = nSrcPar;
    }

    public int getnDstPar() {
        return nDstPar;
    }

    public void setnDstPar(int nDstPar) {
        this.nDstPar = nDstPar;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 0);
        mSrcFilter.create();
        mDstFilter.create();

        // 设置2张图片，一张为原纹理，一张为目标纹理
        int[] texture = EasyGlUtils.genTexturesWithParameter(2, 0, srcBitmap, dstBitmap);
        mSrcFilter.setTextureId(texture[0]);
        mDstFilter.setTextureId(texture[1]);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;

        mSrcFilter.setSize(width, height);
        mDstFilter.setSize(width, height);

        MatrixUtils.getMatrix(mSrcFilter.getMatrix(), MatrixUtils.TYPE_FITSTART,
                srcBitmap.getWidth(), srcBitmap.getHeight(),
                width, height);
        MatrixUtils.getMatrix(mDstFilter.getMatrix(), MatrixUtils.TYPE_FITSTART,
                dstBitmap.getWidth(), dstBitmap.getHeight(),
                width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        // 开启混合  家里
        GLES20.glEnable(GLES20.GL_BLEND);
        // 设置混合函数，第一个参数为源混合因子，第二个参数为目的混合因子
        GLES20.glBlendFunc(nSrcPar, nDstPar);
        // 设置混合方程式，GLES2.0中有三种, GL_MIN和GL_MAX是在OpenGLES3.0才有的。
        GLES20.glBlendEquation(equaInt[nEquaIndex]);


        GLES20.glViewport(0, 0, width, height);
        // 先渲染目的纹理出来，再渲染源纹理出来，是源纹理去与目的纹理混合
        mDstFilter.draw();
        mSrcFilter.draw();
    }
}
