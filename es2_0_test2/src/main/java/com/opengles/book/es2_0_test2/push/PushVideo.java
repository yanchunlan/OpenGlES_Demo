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

    public void initLivePush(String url)
    {
        if(!TextUtils.isEmpty(url))
        {
            initPush(url);
        }
    }


    private native void initPush(String pushUrl);

    // --------------------------------------------------------------

    public void setConnectListener(PushConnectListener connectListener) {
        mConnectListener = connectListener;
    }

    private void onConnecting()
    {
        if(mConnectListener != null)
        {
            mConnectListener.onConnecting();
        }
    }

    private void onConnectSuccess()
    {
        if(mConnectListener != null)
        {
            mConnectListener.onConnectSuccess();
        }
    }

    private void onConnectFail(String msg)
    {
        if(mConnectListener != null)
        {
            mConnectListener.onConnectFail(msg);
        }
    }
}
