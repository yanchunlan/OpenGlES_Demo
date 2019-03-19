package com.ycl.es2_0_native;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ycl.es2_0_native.opengl.MySurfaceView;
import com.ycl.es2_0_native.opengl.NativeOpengl;

import java.nio.ByteBuffer;

public class NativeActivity extends AppCompatActivity {

    private MySurfaceView mMySurfaceView;
    private NativeOpengl mNativeOpengl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        initView();
    }

    private void initView() {
        mMySurfaceView = (MySurfaceView) findViewById(R.id.mySurfaceView);

        mNativeOpengl = new NativeOpengl();
        mMySurfaceView.setNativeOpengl(mNativeOpengl);


        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic);
        if (bitmap != null) {
            ByteBuffer buffer = ByteBuffer.allocate(bitmap.getHeight() * bitmap.getWidth() * 4);
            bitmap.copyPixelsToBuffer(buffer);
            buffer.flip();
            byte[] pixels = buffer.array();
            mNativeOpengl.imgData(bitmap.getWidth(), bitmap.getHeight(), pixels.length, pixels);
        }
    }
}
