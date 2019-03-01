package com.ycl.es2_0_native;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ycl.es2_0_native.opengl.MySurfaceView;
import com.ycl.es2_0_native.opengl.NativeOpengl;

public class NativeActivity extends AppCompatActivity {

    private MySurfaceView mMySurfaceView;
    private NativeOpengl mNativeOpengl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
    }

    private void initView() {
        mMySurfaceView = (MySurfaceView) findViewById(R.id.mySurfaceView);

        mNativeOpengl = new NativeOpengl();
        mMySurfaceView.setNativeOpengl(mNativeOpengl);
    }
}
