package com.opengles.book.es2_0.base;

import android.opengl.GLES20;
import android.util.Log;
import android.view.View;

import com.opengles.book.es2_0.render.render.CubeRenderer;

import java.lang.reflect.Constructor;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-10-28 14:48
 * desc:  包装类
 */
public class MyRenderer extends BaseRenderer {
    private static final String TAG = "MyRenderer";
    public static final String PARAMS = "params";

    private Class<? extends BaseRenderer> rendererClass = CubeRenderer.class;
    private BaseRenderer baseRenderer;

    public MyRenderer(View view) {
        super(view);
    }

    public void setRendererClass(Class<? extends BaseRenderer> rendererClass) {
        this.rendererClass = rendererClass;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated: ");
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1f);

        try {
            Constructor constructor = rendererClass.getDeclaredConstructor(View.class);
            constructor.setAccessible(true);
            baseRenderer = (BaseRenderer) constructor.newInstance(getView());
        } catch (Exception e) {
            e.printStackTrace();
            baseRenderer = new CubeRenderer(getView());
        }

        baseRenderer.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged: ");
        GLES20.glViewport(0, 0, width, height);
        baseRenderer.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.i(TAG, "onDrawFrame: ");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        baseRenderer.onDrawFrame(gl);
    }
}
