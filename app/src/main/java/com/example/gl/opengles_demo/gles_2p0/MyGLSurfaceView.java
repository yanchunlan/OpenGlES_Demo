package com.example.gl.opengles_demo.gles_2p0;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * author: ycl
 * date: 2018-09-25 17:19
 * desc:
 */
public class MyGLSurfaceView extends GLSurfaceView {

    public MyGLSurfaceView(Context context) {
        this(context, null);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);

        MyGLRenderer renderer = new MyGLRenderer();
        setRenderer(renderer);
    }
}
