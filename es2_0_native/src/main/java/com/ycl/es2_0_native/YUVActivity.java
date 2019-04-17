package com.ycl.es2_0_native;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ycl.es2_0_native.opengl.MySurfaceView;
import com.ycl.es2_0_native.opengl.NativeOpengl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class YUVActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "YUVActivity";
    private MySurfaceView mMysurfaceview;
    private Button mBtnPlay;
    private Button mBtnStop;

    private NativeOpengl mNativeOpengl;
    private boolean isExit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv);
        initView();
        initData();
    }

    private void initData() {
        mNativeOpengl = new NativeOpengl();
        mMysurfaceview.setNativeOpengl(mNativeOpengl);
    }

    private void initView() {
        mMysurfaceview = (MySurfaceView) findViewById(R.id.mysurfaceview);
        mBtnPlay = (Button) findViewById(R.id.btn_play);
        mBtnStop = (Button) findViewById(R.id.btn_stop);

        mBtnPlay.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_play) {
            play();
        } else if (i == R.id.btn_stop) {
            stop();
        }
    }

    private void stop() {
        isExit = true;
    }

    private void play() {
        final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/sintel_640_360.yuv");
        if (file==null||!file.exists()) {
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
                                Log.d(TAG, "run: break");
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
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
