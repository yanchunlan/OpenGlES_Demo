package com.opengles.book.es2_0.etc;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-12 22:18
 * desc:
 */
public class ZipGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer{
    public ZipGlSurfaceView(Context context) {
        this(context,null);
    }

    public ZipGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }





    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
