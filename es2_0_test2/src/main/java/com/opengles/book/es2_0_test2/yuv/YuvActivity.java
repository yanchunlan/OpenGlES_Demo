package com.opengles.book.es2_0_test2.yuv;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.opengles.book.es2_0_test2.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class YuvActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "YuvActivity";
    private YuvSurfaceView mYuvview;
    private Button mBtnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv);
        initView();
    }

    private void initView() {
        mYuvview = (YuvSurfaceView) findViewById(R.id.yuvview);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_start) {
            final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sintel_640_360.yuv");
            if (!file.exists()) {
                Toast.makeText(this, "yuv文件不存在", Toast.LENGTH_SHORT).show();
                return;
            }
            AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        FileInputStream fis = new FileInputStream(file);
                        int w = 640;
                        int h = 360;
                        byte[] y = new byte[w * h];
                        byte[] u = new byte[w * h / 4];
                        byte[] v = new byte[w * h / 4];

                        while (true) {
                            int ry = fis.read(y);
                            int ru = fis.read(u);
                            int rv = fis.read(v);
                            if (ry > 0 && ru > 0 && rv > 0) {
                                mYuvview.setFrameData(w, h, y, u, v);
                                Thread.sleep(40);
                            } else {
                                Log.d(TAG, "run: break");
                                break;
                            }
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
