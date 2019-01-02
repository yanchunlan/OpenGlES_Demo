package com.opengles.book.es2_0_test2.surfaceview;

import android.content.Context;
import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

import com.opengles.book.es2_0_test2.R;
import com.opengles.book.es2_0_test2.utils.EglSurfaceView;

public class SurfaceViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MySurfaceView surfaceView = new MySurfaceView(this);
        setContentView(surfaceView);
    }

    class MySurfaceView extends EglSurfaceView {
        public MySurfaceView(Context context) {
            this(context, null);
        }

        public MySurfaceView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setRenderer(new MyRenderer());
            setRenderMode(EglSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
    }

    class MyRenderer implements EglSurfaceView.EglRenderer {
        private static final String TAG = "MyRenderer";

        @Override
        public void onSurfaceCreated() {
            Log.d(TAG, "onSurfaceCreated: ");
        }

        @Override
        public void onSurfaceChanged(int width, int height) {
            Log.d(TAG, "onSurfaceChanged: ");
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame() {
            Log.d(TAG, "onDrawFrame: ");
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
        }
    }

}
