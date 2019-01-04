package com.opengles.book.es2_0_test2.egl;

import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.opengles.book.es2_0_test2.eglUtils.EglHelper;

public class EGLActivity extends AppCompatActivity {

    private SurfaceView surfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surfaceView = new SurfaceView(this);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, final int width, final int height) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EglHelper eglHelper = new EglHelper();
                            eglHelper.initEgl(null, null );

                            while (true) {
                                GLES20.glViewport(0, 0, width, height);
                                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                                GLES20.glClearColor(1, 0, 0, 1);

                                eglHelper.swapBuffers();

                                try {
                                    Thread.sleep(16);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }).start();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        setContentView(surfaceView);
    }
}
