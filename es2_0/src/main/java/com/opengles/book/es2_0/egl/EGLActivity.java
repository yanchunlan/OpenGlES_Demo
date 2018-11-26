package com.opengles.book.es2_0.egl;

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

public class EGLActivity extends AppCompatActivity implements View.OnClickListener, GLES20BackEnv.CallBack {
    private static final String TAG = "EGLActivity";
    private static final int REQUEST_CODE = 1;

    private ImageView mImageView;
    private Button mButton;

    private GLES20BackEnv mBackEnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl);
        initView();
    }

    private void initView() {
        mImageView = (ImageView) findViewById(R.id.imageView);
        mButton = (Button) findViewById(R.id.button);

        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackEnv != null) mBackEnv.destroy();
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

            mBackEnv = new GLES20BackEnv(this, imagePath);
            mBackEnv.setCallBack(this).draw();

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
                Toast.makeText(EGLActivity.this, "保存成功: " + imagePath, Toast.LENGTH_SHORT).show();
                if (bitmap != null) {
                    mImageView.setImageBitmap(bitmap);
                }
            }
        });
    }

    @Override
    public void callFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EGLActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
