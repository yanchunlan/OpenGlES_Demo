package com.opengles.book.es2_0_test2.image_video;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.opengles.book.es2_0_test2.BuildConfig;
import com.opengles.book.es2_0_test2.R;
import com.opengles.book.es2_0_test2.encodec.MediaEncodec;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnPreparedListener;
import com.ywl5320.listener.OnShowPcmDataListener;

import java.io.File;
import java.io.IOException;

public class ImgVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImgVideoSurfaceView mImgvideoview;
    private Button mBtnStart;

    private MediaEncodec mediaEncodec;
    private WlMusic wlMusic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_video);
        initView();
        initData();
    }

    private void initData() {
        mImgvideoview.setCurrentImg(R.drawable.img_1);

        wlMusic = WlMusic.getInstance();
        wlMusic.setCallBackPcmData(true);

        wlMusic.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                wlMusic.playCutAudio(0, 60);
            }
        });

        wlMusic.setOnShowPcmDataListener(new OnShowPcmDataListener() {
            @Override
            public void onPcmInfo(int samplerate, int bit, int channels) {
                mediaEncodec = new MediaEncodec(ImgVideoActivity.this, mImgvideoview.getFbotextureid());
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/image_video.mp4");
                if (file.isFile() && !file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (file == null) {
                    return;
                }

                mediaEncodec.initEncodec(mImgvideoview.getEglContext(),file.getAbsolutePath(),
                        720, 500, samplerate, channels);
                mediaEncodec.startRecord();

                startImgs();
            }

            @Override
            public void onPcmData(byte[] pcmdata, int size, long clock) {
                if (mediaEncodec != null) {
                    mediaEncodec.putPCMData(pcmdata, size);
                }
            }
        });
    }

    private void initView() {
        mImgvideoview = (ImgVideoSurfaceView) findViewById(R.id.imgvideoview);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_start) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/the girl.m4a");
            if (!file.exists()) {
                Toast.makeText(this, "声音文件不存在", Toast.LENGTH_SHORT).show();
                return;
            }

            wlMusic.setSource(file.getAbsolutePath());
            wlMusic.prePared();
        }
    }

    /**
     * 遍历一张纸图片，绘制图片，并编码成mp4
     */
    private void startImgs() {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 10; i++) {
                    int imgsrc = getResources().getIdentifier("img_" + i, "drawable", BuildConfig.APPLICATION_ID);
                    mImgvideoview.setCurrentImg(imgsrc);
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (mediaEncodec != null) {
                    wlMusic.stop();
                    mediaEncodec.stopRecord();
                    mediaEncodec = null;
                }
            }
        });
    }
}
