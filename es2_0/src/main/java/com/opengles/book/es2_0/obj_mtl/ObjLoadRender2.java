package com.opengles.book.es2_0.obj_mtl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.opengles.book.es2_0.filter.obj.ObjFilter2;
import com.opengles.book.es2_0.obj.ObjReader;
import com.opengles.book.es2_0.obj.bean.Obj3D;
import com.opengles.book.es2_0.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author:  ycl
 * date:  2018/12/6 11:51
 * desc:
 */
public class ObjLoadRender2 implements GLSurfaceView.Renderer {
    private List<ObjFilter2> filters;


    public ObjLoadRender2(Context context) {
        List<Obj3D> model = ObjReader.readMultiObj(context, ObjReader.pikachu);
        filters = new ArrayList<>();
        for (Obj3D obj3D : model) {
            ObjFilter2 f = new ObjFilter2(context.getResources());
            f.setObj3D(obj3D);
            filters.add(f);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        for (ObjFilter2 f : filters) {
            f.create();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        for (ObjFilter2 f : filters) {
            f.onSizeChanged(width, height);
            // 所有轴 放大 0.2f
            float[] matrix = MatrixUtils.getOriginalMatrix();
//            MatrixUtils.scale(matrix, 0.2f, 0.2f * width / height, 0.2f);
            Matrix.translateM(matrix,0,0,-0.3f,0);
            Matrix.scaleM(matrix,0,0.008f,0.008f*width/height,0.008f);
            f.setMatrix(matrix);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        for (ObjFilter2 f : filters) {
            // 绕y轴旋转
            MatrixUtils.rotate(f.getMatrix(), 0.3f, 0, 1, 0);
            f.draw();
        }
    }
}
