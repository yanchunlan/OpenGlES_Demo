package com.example.gl.opengles_demo.day03.tex;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;

import com.example.gl.opengles_demo.R;
import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import org.jetbrains.annotations.Nullable;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-04 16:03
 * desc: 纹理
 */
public class MyRendererTex extends AbstractMyRenderer {
    private Resources resources;


    public MyRendererTex(@Nullable Resources resources) {
        this.resources = resources;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
//        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_DEPTH_TEST);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清除颜色缓冲区
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        //设置绘图颜色
        gl.glColor4f(1f, 0f, 0f, 1f);

        //操作模型视图矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        //设置眼球的参数
        GLU.gluLookAt(gl, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f);

        // 旋转角度   绕x顺时针旋转90度
        gl.glRotatef(xrotate, 1f, 0, 0);
        gl.glRotatef(yrotate, 0, 1f, 0);

        onDrawChildFrame(gl);
    }

    @Override
    public void onDrawChildFrame(GL10 gl) {
        /****************** 启用纹理 ************************/
        gl.glEnable(GL10.GL_TEXTURE_2D);
        int[] textids = new int[1];
        gl.glGenTextures(1, textids, 0);// 生成纹理id
        int id = textids[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        //纹理环境模式.

        //线性过滤,贴图在放大或缩小时采用何种方式进行处理像素
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        //纹理环绕模式,如果纹理坐标没有落在0-1之间的话,纹理在三个方向上采用何种方式环绕.
        //如果不到1的话,只显示部分贴图.
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT); // 重复
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }

        //
        float[] texCoord = {
                0f,1f,
                1f,1f,

                0f,0f,
                1f,0f
        };//
        // 指定纹理坐标
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, BufferUtils.arr2FloatBuffer(texCoord));
        // 正方形
        float[] rectCoords = {
                -1f,-1f,0,
                1f,-1f,0,
                -1f,1f,0,
                1f,1f,0,
        };
        BufferUtils.drawRect(gl, rectCoords);





    }
}
