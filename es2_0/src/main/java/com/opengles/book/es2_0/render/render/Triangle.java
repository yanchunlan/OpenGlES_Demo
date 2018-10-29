package com.opengles.book.es2_0.render.render;

import android.view.View;

import com.opengles.book.es2_0.base.BaseRenderer;

import org.jetbrains.annotations.NotNull;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-10-28 15:57
 * desc:
 */
public class Triangle extends BaseRenderer {
    public Triangle(@NotNull View view) {
        super(view);
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
