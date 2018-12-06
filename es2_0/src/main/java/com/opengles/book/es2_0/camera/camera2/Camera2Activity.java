package com.opengles.book.es2_0.camera.camera2;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.opengles.book.es2_0.filter.camera2.ZipPkmAnimationFilter;
import com.opengles.book.es2_0.utils.PermissionUtils;

public class Camera2Activity extends AppCompatActivity implements Camera2SurfaceView.CallBack {
    private static final int REQUEST_CODE_PERMISSIONS = 1002;
    private Camera2SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},
                REQUEST_CODE_PERMISSIONS,
                okRunnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("拍照").setTitle("拍照").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("拍照".equals(item.getTitle().toString())) {
            if (mSurfaceView != null) {
                mSurfaceView.takePhoto();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Runnable okRunnable = new Runnable() {
        @Override
        public void run() {
            //  setContentView
            FrameLayout f = new FrameLayout(Camera2Activity.this);
            f.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            mSurfaceView = new Camera2SurfaceView(Camera2Activity.this) {
                @Override
                void onFilterSet(Camera2Renderer controller) {
                    addFilter(controller);
                }
            };
            mSurfaceView.setCallBack(Camera2Activity.this);
            f.addView(mSurfaceView);
            addContentView(f);
            setContentView(f);
        }
    };

    protected void addContentView(FrameLayout f) {

    }

    protected void addFilter(Camera2Renderer controller) {
        if (controller != null) {
            ZipPkmAnimationFilter filter = new ZipPkmAnimationFilter(getResources());
            filter.setAnimation("assets/etczip/output.zip");
            controller.addFilter(filter);
        }
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
                        Toast.makeText(Camera2Activity.this, "没有获得必要的权限", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mSurfaceView != null) {
            mSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSurfaceView != null) {
            mSurfaceView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSurfaceView != null) {
            mSurfaceView.onDestroy();
        }
    }


    @Override
    public void callSuccess(final String imagePath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Camera2Activity.this, "保存成功->" + imagePath, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void callFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Camera2Activity.this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
