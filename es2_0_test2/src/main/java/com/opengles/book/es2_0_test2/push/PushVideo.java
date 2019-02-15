package com.opengles.book.es2_0_test2.push;

import android.text.TextUtils;

/**
 * author:  ycl
 * date:  2019/2/15 15:31
 * desc:
 */
public class PushVideo {

    static {
        System.loadLibrary("native-lib");
    }

    public void initLivePush(String url)
    {
        if(!TextUtils.isEmpty(url))
        {
            initPush(url);
        }
    }

    private native void initPush(String pushUrl);
}
