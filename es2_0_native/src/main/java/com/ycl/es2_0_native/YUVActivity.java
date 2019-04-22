package com.ycl.es2_0_native;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ycl.es2_0_native.opengl.MySurfaceView;
import com.ycl.es2_0_native.opengl.NativeOpengl;
import com.ycl.es2_0_native.utils.PermissionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class YUVActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "YUVActivity";
    private MySurfaceView mMySurfaceView;
    private Button mBtnPlay;
    private Button mBtnStop;

    private NativeOpengl mNativeOpengl;
    private boolean isExit = true;

    private static final int REQUEST_CODE_PERMISSIONS = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_yuv);
        initView();
        initData();
    }

    private void initData() {
        mNativeOpengl = new NativeOpengl();
        mMySurfaceView.setNativeOpengl(mNativeOpengl).setYuv(true);
    }

    private void initView() {
        mMySurfaceView = (MySurfaceView) findViewById(R.id.mySurfaceView);
        mBtnPlay = (Button) findViewById(R.id.btn_play);
        mBtnStop = (Button) findViewById(R.id.btn_stop);

        mBtnPlay.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_play) {
            PermissionUtils.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSIONS,
                    okRunnable);
        } else if (i == R.id.btn_stop) {
            stop();
        }
    }

    private Runnable okRunnable = new Runnable() {
        @Override
        public void run() {
            play();
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
                        Toast.makeText(YUVActivity.this, "没有获得必要的权限", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void stop() {
        isExit = true;
    }

    private void play() {
        // ffmpeg -i input.mp4 -pix_fmt yuv420p out.yuv
        final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/out.yuv");
        if (file == null || !file.exists()) {
            Toast.makeText(this, "yuv文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isExit) {
            isExit = false;

            AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        int w = 640;
                        int h = 360;
                        byte[] y = new byte[w * h];
                        byte[] u = new byte[w * h / 4];
                        byte[] v = new byte[w * h / 4];

                        while (true) {
                            if (isExit) {
                                Log.d(TAG, "run: break");
                                break;
                            }

                            int ysize = fis.read(y);
                            int usize = fis.read(u);
                            int vsize = fis.read(v);
                            if (ysize > 0 && usize > 0 && vsize > 0) {
                                mNativeOpengl.setYuvData(y, u, v, w, h);
                                Thread.sleep(40);
                            } else {
                                isExit = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            fis = null;
                        }
                    }
                }
            });

        }
    }
}
