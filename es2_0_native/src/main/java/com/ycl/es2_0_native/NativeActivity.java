package com.ycl.es2_0_native;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ycl.es2_0_native.opengl.MySurfaceView;
import com.ycl.es2_0_native.opengl.NativeOpengl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class NativeActivity extends AppCompatActivity implements View.OnClickListener {

    private MySurfaceView mMySurfaceView;
    private Button mBtChangeFilter;
    private Button mBtChangeTexture;

    private NativeOpengl mNativeOpengl;

    // params
    private List<Integer> imgs = new ArrayList<>();
    private int index = -1;
    private byte[] pixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        initView();
        initData();
    }

    private void initData() {
        mNativeOpengl = new NativeOpengl();
        mMySurfaceView.setNativeOpengl(mNativeOpengl)
                .setSurfaceCreateListener(new MySurfaceView.OnSurfaceCreateListener() {
                    @Override
                    public void surfaceCreated() {
                        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic);
                        if (bitmap != null) {
                            ByteBuffer buffer = ByteBuffer.allocate(bitmap.getHeight() * bitmap.getWidth() * 4);
                            bitmap.copyPixelsToBuffer(buffer);
                            buffer.flip();
                            byte[] pixels = buffer.array();
                            mNativeOpengl.imgData(bitmap.getWidth(), bitmap.getHeight(), pixels.length, pixels);
                        }
                    }
                });
    }

    private void initView() {
        mMySurfaceView = (MySurfaceView) findViewById(R.id.mySurfaceView);
        mBtChangeFilter = (Button) findViewById(R.id.bt_changeFilter);
        mBtChangeFilter.setOnClickListener(this);
        mBtChangeTexture = (Button) findViewById(R.id.bt_changeTexture);
        mBtChangeTexture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_changeFilter) {

        } else if (i == R.id.bt_changeTexture) {

        }
    }
}
