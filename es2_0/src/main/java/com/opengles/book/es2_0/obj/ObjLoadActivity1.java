package com.opengles.book.es2_0.obj;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.opengles.book.es2_0.R;

public class ObjLoadActivity1 extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        setContentView(mGLSurfaceView);


        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(getRender());
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }


    protected GLSurfaceView.Renderer getRender() {
        return new ObjLoadRender(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}
