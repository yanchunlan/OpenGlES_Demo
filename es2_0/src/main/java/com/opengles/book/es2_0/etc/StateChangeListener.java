package com.opengles.book.es2_0.etc;

/**
 * author: ycl
 * date: 2018-11-12 22:20
 * desc:
 */
public interface StateChangeListener {
    int START = 1;
    int STOP = 2;
    int PLAYING = 3;
    int INIT = 4;
    int PAUSE = 5;
    int RESUME = 6;

    void onStateChanged(int lastState, int nowState);
}
