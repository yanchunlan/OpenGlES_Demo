package com.opengles.book.es2_0_test2.push;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.opengles.book.es2_0_test2.R;

public class PushActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PushActivity";
    private Button mBtnStartPush;
    private PushVideo mPushVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        initView();
    }

    private void initView() {
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
            // 开启nginx之后再开始推流
            mPushVideo.initLivePush("rtmp://119.27.185.134/live/mystream");
        }
    }
}
