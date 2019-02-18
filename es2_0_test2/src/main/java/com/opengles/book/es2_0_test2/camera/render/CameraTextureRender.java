package com.opengles.book.es2_0_test2.camera.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.opengles.book.es2_0_test2.muti.surface.MyTextureRender;
import com.opengles.book.es2_0_test2.utils.BufferUtils;
import com.opengles.book.es2_0_test2.utils.DrawUtils;
import com.opengles.book.es2_0_test2.utils.TextureUtils;

import java.nio.ByteBuffer;

/**
 * author:  ycl
 * date:  2019/1/7 9:50
 * desc:
 */
public class CameraTextureRender extends MyTextureRender {

    private Bitmap bitmap;
    private int bitmapTextureId;

    public CameraTextureRender(Context context) {
        super(context);
    }

    @Override
    protected void initBuffer() {
        //  vertexData 需要重置
        vertexData = new float[]{
                -1f, -1f,
                1f, -1f,
                -1f, 1f,
                1f, 1f,

                0f, 0f, // 预留顶点位置，放置水印
                0f, 0f,
                0f, 0f,
                0f, 0f
        };

        bitmap = DrawUtils.createTextImage("rtmp://192.168.139.128/myapp/mystream", 50,
                "#ff0000", "#00000000", 0);

        // 假设高度占据总高度的1/10，则高度为0.1f,宽度为0.1f*r
        float r = 1.0f * bitmap.getWidth() / bitmap.getHeight();
        float w = r * 0.1f;

        // 假设 绘制x末尾点0.8f,y为0.8f
        vertexData[8] = 0.8f - w;
        vertexData[9] = -0.8f;

        vertexData[10] = 0.8f;
        vertexData[11] = -0.8f;

        vertexData[12] = 0.8f - w;
        vertexData[13] = -0.7f;

        vertexData[14] = 0.8f;
        vertexData[15] = -0.7f;

        vertexBuffer = BufferUtils.arr2FloatBuffer(vertexData);
        fragmentBuffer = BufferUtils.arr2FloatBuffer(fragmentData);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 添加水印需要开启的混合模式及方程式
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        ByteBuffer buffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 4);
        bitmap.copyPixelsToBuffer(buffer);
        buffer.flip(); // 数据翻转  因为纹理坐标系与真实坐标是反转的，所有最好此处反转180

        bitmapTextureId = TextureUtils.genTexturesWithParameter(1, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), buffer)[0];
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        buffer.clear();
    }

    @Override
    protected void onDrawExp() {
        // 绘制水印 bitmap
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bitmapTextureId);
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
                32);
        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexData.length * 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
