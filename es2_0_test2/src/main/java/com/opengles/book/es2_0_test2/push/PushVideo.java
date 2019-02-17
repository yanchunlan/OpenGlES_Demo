package com.opengles.book.es2_0_test2.push;

import android.text.TextUtils;

/**
 * author:  ycl
 * date:  2019/2/15 15:31
 * desc:
 */
public class PushVideo {

    static {
        System.loadLibrary("push-lib");
    }

    private PushConnectListener mConnectListener;

    public void initLivePush(String url) {
        if (!TextUtils.isEmpty(url)) {
            initPush(url);
        }
    }

    public void pushSPSPPS(byte[] sps, byte[] pps) {
        if (sps != null && pps != null) {
            pushSPSPPS(sps, sps.length, pps, pps.length);
        }
    }

    public void pushVideoData(byte[] data, boolean keyframe) {
        if (data != null) {
            pushVideoData(data, data.length, keyframe);
        }
    }

    public void pushAudioData(byte[] data) {
        if (data != null) {
            pushAudioData(data, data.length);
        }
    }

    public void stopPush() {
        pushStop();
    }


    private native void initPush(String pushUrl);

    private native void pushSPSPPS(byte[] sps, int sps_len, byte[] pps, int pps_len);

    private native void pushVideoData(byte[] data, int data_len, boolean keyFrame);

    private native void pushAudioData(byte[] data, int data_len);

    private native void pushStop();

    // --------------------------------------------------------------

    public void setConnectListener(PushConnectListener connectListener) {
        mConnectListener = connectListener;
    }

    private void onConnecting() {
        if (mConnectListener != null) {
            mConnectListener.onConnecting();
        }
    }

    private void onConnectSuccess() {
        if (mConnectListener != null) {
            mConnectListener.onConnectSuccess();
        }
    }

    private void onConnectFail(String msg) {
        if (mConnectListener != null) {
            mConnectListener.onConnectFail(msg);
        }
    }
}
