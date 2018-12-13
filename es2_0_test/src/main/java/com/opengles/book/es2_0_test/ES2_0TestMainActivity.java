package com.opengles.book.es2_0_test;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.opengles.book.es2_0_test.camera.takepic.TakePictureActivity;

public class ES2_0TestMainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},
                REQUEST_CODE_PERMISSIONS,
                okRunnable);
    }

    private Runnable okRunnable = new Runnable() {
        @Override
        public void run() {

//             setContentView(new TriangleGLSurfaceView(ES2_0TestMainActivity.this)); // 绘制三角形

//             setContentView(new SquareGLSurfaceView(ES2_0TestMainActivity.this));  // 绘制正方形

//             setContentView(new OvalGLSurfaceView(ES2_0TestMainActivity.this)); // 绘制圆形

//             setContentView(new PaintPointGLSurfaceView(ES2_0TestMainActivity.this)); // 手绘点

//             setContentView(new RotateTriangleGLSurfaceView(ES2_0TestMainActivity.this)); // 旋转三角形

//             setContentView(new ImageGLSurfaceView(ES2_0TestMainActivity.this)); // 加载图片

//             startActivity(new Intent(ES2_0TestMainActivity.this, PreviewCameraActivity.class));  // OpenGL预览摄像头

            startActivity(new Intent(ES2_0TestMainActivity.this, TakePictureActivity.class));  // OpenGL 拍照

        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode == REQUEST_CODE_PERMISSIONS,
                grantResults,
                okRunnable,
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ES2_0TestMainActivity.this, "没有获得必要的权限", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}
