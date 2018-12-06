package com.opengles.book.es2_0.filter.obj;

import android.content.res.Resources;
import android.opengl.GLES20;

import com.opengles.book.es2_0.filter.AFilter;
import com.opengles.book.es2_0.obj.bean.Obj3D;

/**
 * author:  ycl
 * date:  2018/12/6 11:55
 * desc:
 */
public class ObjFilter extends AFilter {
    private int mHNormal;

    private int textureId;

    private Obj3D obj;

    public ObjFilter(Resources mRes) {
        super(mRes);
    }

    public void setObj3D(Obj3D obj) {
        this.obj = obj;
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("3dres/obj.vert","3dres/obj.frag");
        mHNormal=GLES20.glGetAttribLocation(mProgram,"vNormal");
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
