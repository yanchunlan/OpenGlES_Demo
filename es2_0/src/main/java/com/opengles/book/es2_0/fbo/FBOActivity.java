package com.opengles.book.es2_0.fbo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.opengles.book.es2_0.R;

/**
 * FBO -> Frame Buffer Object 离屏渲染技术
 * 目的： 获取当前图片，并缓冲加GlSurfaceView显示出来，并把缓冲的图片，显示到当前界面ImageView上
 */
public class FBOActivity extends AppCompatActivity implements FBOSurfaceView.CallBack, View.OnClickListener {
    private static final String TAG = "FBOActivity";
    private static final int REQUEST_CODE = 1;
    private FBOSurfaceView mFboSurfaceView;
    private ImageView mImageView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbo);
        initView();
    }

    private void initView() {
        mFboSurfaceView = (FBOSurfaceView) findViewById(R.id.fboSurfaceView);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mButton = (Button) findViewById(R.id.button);

        mButton.setOnClickListener(this);
        mFboSurfaceView.setCallBack(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFboSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFboSurfaceView.onPause();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK && data != null) {
            Uri image = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(image, projection,
                    null, null, null);
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(projection[0]);
            String imagePath = cursor.getString(index);
            Log.i(TAG, "onActivityResult:   --> mImagePath: " + imagePath);
            mFboSurfaceView.setImagePath(imagePath);
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    @Override
    public void callSuccess(final Bitmap bitmap, final String imagePath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FBOActivity.this, "保存成功: "+imagePath, Toast.LENGTH_SHORT).show();
                if (bitmap != null) {
                    mImageView.setImageBitmap(bitmap);
//                    bitmap.recycle();
                }
            }
        });
    }

    @Override
    public void callFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FBOActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
