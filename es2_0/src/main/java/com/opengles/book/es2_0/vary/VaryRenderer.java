package com.opengles.book.es2_0.vary;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.opengles.book.es2_0.utils.VaryTools;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-07 0:26
 * desc:
 */
public class VaryRenderer implements GLSurfaceView.Renderer {

    private VaryTools mVaryTools;
    private Cube mCube;

    public VaryRenderer(Resources res) {
        mVaryTools = new VaryTools();
        mCube = new Cube(res);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        mCube.create();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = width / (float) height;
        mVaryTools.ortho(-ratio * 6, ratio * 6, -6, 6, 3, 20);
        mVaryTools.setLookAtM(0, 0, 10, 0, 0, 0, 0, 1, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mCube.setMatrix(mVaryTools.getFinalMatrix());
        mCube.drawSelf();


        // 此处待测试单位矩阵变换与直接对获取得矩阵进行变换有何区别
        // 区别在于是对一个单位矩阵得变换，变了之后可以回复，如果是原矩阵变了就不能恢复了


        // y平移
        mVaryTools.pushMatrix();
        mVaryTools.translate(0, 3, 0);
        mCube.setMatrix(mVaryTools.getFinalMatrix());
        // 与创建的current的变换效果是一致的
        /*  Matrix.translateM(mCube.getMatrix(), 0, 0, 3, 0);*/
        mCube.drawSelf();
        mVaryTools.popMatrix();

        // y平移,xyz->(0,0,0)到(1,1,1)旋转30度
        mVaryTools.pushMatrix();
        mVaryTools.translate(0, -3, 0);
        mVaryTools.rotate(30f, 1, 1, 1);
        mCube.setMatrix(mVaryTools.getFinalMatrix());
        mCube.drawSelf();
        mVaryTools.popMatrix();


        // x轴负方向平移，然后按xyz->(0,0,0)到(1,-1,1)旋转120度，在放大到0.5倍
        mVaryTools.pushMatrix();
        mVaryTools.translate(-3, 0, 0);
        mVaryTools.scale(0.5f, 0.5f, 0.5f);

        //在以上变换的基础上再进行变换
        mVaryTools.pushMatrix();
        mVaryTools.translate(12, 0, 0);
        mVaryTools.scale(1.0f, 2.0f, 1.0f);
        mVaryTools.rotate(30f, 1, 2, 1);
        mCube.setMatrix(mVaryTools.getFinalMatrix());
        mCube.drawSelf();
        mVaryTools.popMatrix();

        //接着被中断的地方执行
        mVaryTools.rotate(30f, -1, -1, 1);
        mCube.setMatrix(mVaryTools.getFinalMatrix());
        mCube.drawSelf();
        mVaryTools.popMatrix();
    }
}
