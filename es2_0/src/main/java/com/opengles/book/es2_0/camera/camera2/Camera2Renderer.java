package com.opengles.book.es2_0.camera.camera2;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;

import com.opengles.book.es2_0.camera.camera2.camera_render.GLRenderer;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-26 22:39
 * desc:
 */
public class Camera2Renderer implements GLRenderer {
    private static final String TAG = "Camera2Renderer";
    private Context mContext;

    private GLRenderer mRenderer;
    private Camera2Renderer.CallBack mCallBack;
    private int frameCallBackWidth, frameCallBackHeight; // 回调数据的宽高

    public Camera2Renderer(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }


    //   ------------  callback回调  start ---------------
    public void surfaceCreated(SurfaceHolder holder) {

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    //   ------------  callback回调  end ---------------

    public void onResume() {

    }

    public void onPause() {
    }

    public void destroy() {
    }

    public void setCallBackSize(int w, int h) {
        this.frameCallBackWidth = w;
        this.frameCallBackHeight = h;
        Log.i(TAG, "setCallBackSize:  ->    w: " + w + " h: " + h);
    }

    public void setCallBack(Camera2Renderer.CallBack callBack) {
        mCallBack = callBack;
    }

    public void setRenderer(GLRenderer renderer) {
        mRenderer = renderer;
    }

    interface CallBack {
        public void onFrame(ByteBuffer buffer, int w, int h);
    }
}
