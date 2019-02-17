package com.opengles.book.es2_0_test2.push;

/**
 * author: ycl
 * date: 2019-02-17 11:43
 * desc: rtmp推流连接回调
 */
public interface PushConnectListener {
    void onConnecting();

    void onConnectSuccess();

    void onConnectFail(String msg);
}
