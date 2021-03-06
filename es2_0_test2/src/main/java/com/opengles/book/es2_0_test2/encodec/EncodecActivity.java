package com.opengles.book.es2_0_test2.encodec;

import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.opengles.book.es2_0_test2.camera.CameraActivity;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnCompleteListener;
import com.ywl5320.listener.OnPreparedListener;
import com.ywl5320.listener.OnShowPcmDataListener;

import java.io.File;
import java.io.IOException;

public class EncodecActivity extends CameraActivity {
    private static final String TAG = "EncodecActivity";
    private Button btn_Record;

    private WlMusic wlMusic;
    private MediaEncodec mediaEncodec;

    @Override
    protected void addContentView(FrameLayout f) {
        ViewGroup.LayoutParams p = mCameraSurfaceView.getLayoutParams();
        if (p instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams params = ((FrameLayout.LayoutParams) p);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mCameraSurfaceView.setLayoutParams(params);
        }

        btn_Record = new Button(this);
        btn_Record.setText("开始录制");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        btn_Record.setLayoutParams(params);
        f.addView(btn_Record);

        initListener();
    }

    private void initListener() {
        // 6.0以上手机貌似会奔溃
        wlMusic = WlMusic.getInstance();
        wlMusic.setCallBackPcmData(true);
        wlMusic.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                wlMusic.playCutAudio(39, 60);
            }
        });

        wlMusic.setOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete() {
                if (mediaEncodec != null) {
                    mediaEncodec.stopRecord();
                    mediaEncodec = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn_Record.setText("开始录制");
                        }
                    });
                }
            }
        });

        wlMusic.setOnShowPcmDataListener(new OnShowPcmDataListener() {
            @Override
            public void onPcmInfo(int samplerate, int bit, int channels) {
                Log.d(TAG, "onPcmInfo: samplerate: " + samplerate + " bit: " + bit + " channels: " + channels);
                Log.d(TAG, "onPcmInfo: textureId: " + mCameraSurfaceView.getTextureId());
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/live_pusher.mp4");
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
                mediaEncodec = new MediaEncodec(EncodecActivity.this, mCameraSurfaceView.getTextureId());
                mediaEncodec.initEncodec(mCameraSurfaceView.getEglContext(),file.getAbsolutePath(),
                        720, 1280, samplerate, channels);
                mediaEncodec.setOnMediaInfoListener(new BaseMediaEncoder.OnMediaInfoListener() {
                    @Override
                    public void onMediaTime(int times) {
                        Log.d(TAG, "time is : " + times);
                    }
                });
                mediaEncodec.startRecord();
            }

            @Override
            public void onPcmData(byte[] pcmdata, int size, long clock) {
                Log.d(TAG, "onPcmData: size: " + size + " clock: " + clock);
                if (mediaEncodec != null) {
                    mediaEncodec.putPCMData(pcmdata, size);
                }
            }
        });

        btn_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaEncodec == null) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/the girl.m4a");
                    if (!file.exists()) {
                        Toast.makeText(EncodecActivity.this, "声音文件不存在", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    wlMusic.setSource(file.getAbsolutePath());
                    wlMusic.prePared();
                    btn_Record.setText("正在录制");
                } else {
                    mediaEncodec.stopRecord();
                    btn_Record.setText("开始录制");
                    mediaEncodec = null;
                    wlMusic.stop();
                }
            }
        });
    }
}
