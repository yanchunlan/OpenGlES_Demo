package com.ycl.es2_0_native;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NativeActivity extends AppCompatActivity {
    static {
        System.loadLibrary("native-lib");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
    }

    public native String stringFromJNI();
}
