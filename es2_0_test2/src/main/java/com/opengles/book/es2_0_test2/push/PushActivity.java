package com.opengles.book.es2_0_test2.push;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.opengles.book.es2_0_test2.R;
import com.opengles.book.es2_0_test2.camera.CameraSurfaceView;
import com.opengles.book.es2_0_test2.push.codec.BasePushEncoder;
import com.opengles.book.es2_0_test2.push.codec.PushEncodec;

public class PushActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PushActivity";
    private CameraSurfaceView mCameraSurfaceView;
    private Button mBtnStartPush;
    private PushVideo mPushVideo;

    private boolean start = false;
    private PushEncodec mPushEncodec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        initView();
    }

    private void initView() {
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        mBtnStartPush = (Button) findViewById(R.id.btn_start_push);

        mBtnStartPush.setOnClickListener(this);

        mPushVideo = new PushVideo();
        mPushVideo.setConnectListener(new PushConnectListener() {
            @Override
            public void onConnecting() {
                Log.d(TAG, "onConnecting: ");
            }

            @Override
            public void onConnectSuccess() {
                Log.d(TAG, "onConnectSuccess: ");
                // RTMP连接成功nginx服务器之后，就开始推流

                mPushEncodec = new PushEncodec(PushActivity.this, mCameraSurfaceView.getTextureId());
                mPushEncodec.initEncodec(mCameraSurfaceView.getEglContext(), 720/2, 1280/2,
                        44100, 2);
                mPushEncodec.setOnMediaInfoListener(new BasePushEncoder.OnMediaInfoListener() {
                    @Override
                    public void onMediaTime(int times) {
                        Log.d(TAG, "time is : " + times);
                    }

                    @Override
                    public void onSPSPPSInfo(byte[] sps, byte[] pps) {
                        mPushVideo.pushSPSPPS(sps, pps);
                    }

                    @Override
                    public void onVideoInfo(byte[] data, boolean keyFrame) {
                        mPushVideo.pushVideoData(data, keyFrame);
                    }

                    @Override
                    public void onAudioInfo(byte[] data) {
                        mPushVideo.pushAudioData(data);
                    }
                });
                mPushEncodec.startRecord();
            }

            @Override
            public void onConnectFail(String msg) {
                Log.d(TAG, "onConnectFail: " + msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_start_push) {

            start = !start;
            if (start) {

                mBtnStartPush.setText("关闭推流");
                // 开启nginx之后再开始推流
                mPushVideo.initLivePush("rtmp://119.27.185.134/live/mystream");
            } else {

                mBtnStartPush.setText("开始推流");
                mPushVideo.stopPush();
                // 关闭流
                if (mPushEncodec != null) {
                    mPushEncodec.stopRecord();
                    mPushEncodec = null;
                }
            }

        }
    }
}
