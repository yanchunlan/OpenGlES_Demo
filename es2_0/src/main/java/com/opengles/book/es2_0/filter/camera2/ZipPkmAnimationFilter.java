package com.opengles.book.es2_0.filter.camera2;

import android.content.res.Resources;
import android.opengl.ETC1;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.util.Log;

import com.opengles.book.es2_0.etc.ZipPkmReader;
import com.opengles.book.es2_0.filter.AFilter;
import com.opengles.book.es2_0.utils.MatrixUtils;

import java.nio.ByteBuffer;

/**
 * author:  ycl
 * date:  2018/11/29 11:25
 * desc:
 */
public class ZipPkmAnimationFilter extends AFilter {
    private ZipPkmReader mPkmReader;
    private NoFilter mNoFilter;

    private int glTextureAlpha;
    private int[] texture;

    private ByteBuffer emptyBuffer;
    private int width, height;
    private boolean isPlay = false;


    public ZipPkmAnimationFilter(Resources mRes) {
        super(mRes);
        mPkmReader = new ZipPkmReader(mRes.getAssets());
        mNoFilter = new NoFilter(mRes);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/pkm_mul.vert", "shader/pkm_mul.frag");
        glTextureAlpha = GLES20.glGetUniformLocation(mProgram, "vTextureAlpha");


        texture = new int[2];
        createTextureId(texture);
        setTextureId(texture[0]);

        mNoFilter.create();
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        // 因为不同的宽高，创建ETC1Texture 需要的buffer不同，所有在此处设置
        this.emptyBuffer = ByteBuffer.allocateDirect(ETC1.getEncodedDataSize(width, height));
        this.width = width;
        this.height = height;
        // 开启混合
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        mNoFilter.setSize(width, height);
    }

    @Override
    public float[] getMatrix() {
        return mNoFilter.getMatrix();
    }


    @Override
    protected void onBindTexture() {
        ETC1Util.ETC1Texture t = mPkmReader.getNextTexture();
        ETC1Util.ETC1Texture tAlpha = mPkmReader.getNextTexture();
        Log.d("", "is ETC null->" + (t == null));
        if (t != null && tAlpha != null) {
            // 此处因为图片转换为ETC1Texture 了，所有直接获取其宽高就代表bitmap的宽高了
            //根据不同的type设置不同的矩阵变换，显示不同的图片样式
            MatrixUtils.getMatrix(super.getMatrix(), MatrixUtils.TYPE_FITEND,
                    t.getWidth(), t.getHeight(), width, height);
//            MatrixUtils.flip(super.getMatrix(), false, true);
            onSetExpandData();

            // bind  texture
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + getTextureType()); // 设置texture0通道
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            // etc需要的load加载
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_SHORT_5_6_5, t);
            GLES20.glUniform1i(mHTexture, getTextureType());

            // bind  textureAlpha
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + getTextureType()); // 设置texture0通道
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[1]);
            // etc需要的load加载
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_SHORT_5_6_5, tAlpha);
            GLES20.glUniform1i(glTextureAlpha, 1 + getTextureType());
        } else {
            //  在重绘的时候就不断去读取下一个贞图
            if (mPkmReader != null) {
                mPkmReader.close();
                mPkmReader.open();
            }

            // 如果是null，则不需要设置matrix，且赋值空对象的 ETC1Util.ETC1Texture
            onSetExpandData();

            // bind  texture
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + getTextureType()); // 设置texture0通道
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            // etc需要的load加载
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_SHORT_5_6_5, new ETC1Util.ETC1Texture(width, height, emptyBuffer));
            GLES20.glUniform1i(mHTexture, getTextureType());

            // bind  textureAlpha
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + getTextureType()); // 设置texture0通道
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[1]);
            // etc需要的load加载
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_SHORT_5_6_5, new ETC1Util.ETC1Texture(width, height, emptyBuffer));
            GLES20.glUniform1i(glTextureAlpha, 1 + getTextureType());
            isPlay = false;
        }

    }

    @Override
    protected void onDraw() {
        if (getTextureId() != 0) {
            mNoFilter.setTextureId(getTextureId());
            mNoFilter.draw();
        }
        GLES20.glViewport(100, 0, width / 6, height / 6);
        super.onDraw();
        GLES20.glViewport(0, 0, width, height);
    }

    public void setAnimation(String path) {
        if (mPkmReader != null) {
            this.mPkmReader.setPath(path);
            this.mPkmReader.open();
        }
    }


    @Override
    protected void finalize() throws Throwable {
        if (mPkmReader != null) {
            mPkmReader.close();
        }
        super.finalize();
    }

    // 此处创建2个，是因为一个是texture，一个是透明度的，总结就是2个
    private void createTextureId(int[] texture) {
        // 此处注意： n代表个数，target代表类型，一般是GLES20.GL_TEXTURE_2D 类型
        GLES20.glGenTextures(2, texture, 0);
        for (int i = 0; i < texture.length; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[i]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
    }
}
