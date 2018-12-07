package com.opengles.book.es2_0.obj;

import android.opengl.GLSurfaceView;

public class ObjLoadActivity1 extends BaseGlSVActivity {

    @Override
    protected GLSurfaceView.Renderer getRender() {
        return new ObjLoadRender(this);
    }
}
