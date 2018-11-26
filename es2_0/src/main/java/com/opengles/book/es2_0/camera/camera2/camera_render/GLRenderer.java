package com.opengles.book.es2_0.camera.camera2.camera_render;

import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;

/**
 * author: ycl
 * date: 2018-11-26 22:54
 * desc:
 */
public interface GLRenderer extends GLSurfaceView.Renderer {
    void surfaceDestroyed(SurfaceHolder holder);
}
