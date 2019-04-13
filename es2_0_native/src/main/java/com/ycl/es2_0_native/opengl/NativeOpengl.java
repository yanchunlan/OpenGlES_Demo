package com.ycl.es2_0_native.opengl;

import android.view.Surface;

/**
 * author:  ycl
 * date:  2019/3/1 18:49
 * desc:
 */
public class NativeOpengl {
    static {
        System.loadLibrary("native-lib");
    }
    public native void surfaceCreate(Surface surface);

    public native void surfaceChange(int width, int height);

    public native void surfaceDestroy( );

    public native void imgData(int w, int h, int length, byte[] data);
}
