package com.opengles.book.es2_0.obj;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.opengles.book.es2_0.filter.obj.ObjFilter;
import com.opengles.book.es2_0.obj.bean.Obj3D;
import com.opengles.book.es2_0.utils.MatrixUtils;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author:  ycl
 * date:  2018/12/6 11:51
 * desc:
 */
public class ObjLoadRender implements GLSurfaceView.Renderer {
    private ObjFilter mFilter;
    private Obj3D obj;


    public ObjLoadRender(Context c) {
        mFilter = new ObjFilter(c.getResources());
        obj = new Obj3D();
        try {
            // 加载数据到obj里面去
            ObjReader.read(c.getResources().getAssets().open(ObjReader.hat), obj);
            mFilter.setObj3D(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFilter.create();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mFilter.onSizeChanged(width, height);
        // 所有轴 放大 0.2f
        float[] matrix = MatrixUtils.getOriginalMatrix();
        MatrixUtils.scale(matrix, 0.2f, 0.2f * width / height, 0.2f);
        mFilter.setMatrix(matrix);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 绕y轴旋转
        MatrixUtils.rotate(mFilter.getMatrix(), 0.3f, 0, 1, 0);
        mFilter.draw();
    }
}
