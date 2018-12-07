package com.opengles.book.es2_0.obj_mtl;

import android.opengl.GLSurfaceView;

import com.opengles.book.es2_0.obj.ObjLoadActivity1;

public class ObjLoadActivity2 extends ObjLoadActivity1 {

    @Override
    protected GLSurfaceView.Renderer getRender() {
        return new ObjLoadRender2(this);
    }
}
