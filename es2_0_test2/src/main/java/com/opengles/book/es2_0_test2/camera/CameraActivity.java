package com.opengles.book.es2_0_test2.camera;

import android.Manifest;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.opengles.book.es2_0_test2.R;
import com.opengles.book.es2_0_test2.utils.PermissionUtils;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    protected CameraSurfaceView mCameraSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.requestPermissions(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},
                REQUEST_CODE_PERMISSIONS,
                okRunnable);
    }

    private Runnable okRunnable = new Runnable() {
        @Override
        public void run() {
            FrameLayout f = new FrameLayout(CameraActivity.this);
            f.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));

            mCameraSurfaceView = new CameraSurfaceView(CameraActivity.this);
            // 假设宽高用于适配
            mCameraSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(
                    360  , // px
                    640
            ));

            f.addView(mCameraSurfaceView);
            addContentView(f); // 子类继续实现需要的效果
            setContentView(f);
        }
    };

    protected void addContentView(FrameLayout f ) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode == REQUEST_CODE_PERMISSIONS,
                grantResults,
                okRunnable,
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, "没有获得必要的权限", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSurfaceView != null) {
            mCameraSurfaceView.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mCameraSurfaceView != null) {
            mCameraSurfaceView.previewAngle(this);
        }
    }
}
