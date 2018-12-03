package com.opengles.book.es2_0.camera.camera2;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.opengles.book.es2_0.camera.camera2.camera_render.GLRenderer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * author: ycl
 * date: 2018-11-26 22:51
 * desc:  代理render  -> Camera2Renderer
 */
public abstract class Camera2SurfaceView extends SurfaceView implements
        SurfaceHolder.Callback,
        Camera2Renderer.CallBack {

    private GLRenderer mRenderer;  // 相机初始化的类
    private Camera2Renderer mController; // openGl渲染类

    private Camera2SurfaceView.CallBack mCallBack;
    private Context mContext;


    public Camera2SurfaceView(Context context) {
        this(context, null);
    }

    public Camera2SurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Camera2SurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initController();
        initRenderer();
        getHolder().addCallback(this);
    }

    private void initRenderer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 大于21 ，即大于5.0就用camera2
            mRenderer = new com.opengles.book.es2_0.camera.camera2.camera_render.Camera2Renderer(getContext(), mController);
        } else {
            // 小于21 ，即小于5.0就用camera1
            mRenderer = new com.opengles.book.es2_0.camera.camera2.camera_render.Camera1Renderer(mController);
        }
    }

    private void initController() {
        mController = new Camera2Renderer(mContext);
        onFilterSet(mController);
        mController.setCallBackSize(this.getWidth(), this.getHeight());
        mController.setCallBack(this);
    }

    // 必须实现的方法
    abstract void onFilterSet(Camera2Renderer controller);

    // 代理接口
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mController != null) {
            mController.surfaceCreated(holder);
            mController.setRenderer(mRenderer);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mController != null) {
            mController.surfaceChanged(holder, format, width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mController != null) {
            mController.surfaceDestroyed(holder);
        }
    }

    protected void takePhoto() {
        if (mController != null) {
            mController.takePhoto();
        }
    }

    protected void onResume() {
        if (mController != null) {
            mController.onResume();
        }
    }

    protected void onPause() {
        if (mController != null) {
            mController.onPause();
        }
    }

    protected void onDestroy() {
        if (mController != null) {
            mController.destroy();
        }
    }

    @Override
    public void onFrame(final byte[] buffer, final int w, final int h) {
        // onDraw 里面返回
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(buffer)); // buffer 转化为bitmap
                save(bitmap);
                bitmap.recycle();
            }
        });
    }

    // 保存bitmap成为本地文件
    private void save(Bitmap bitmap) {
        String folderPath = mContext.getExternalCacheDir().getAbsolutePath() + "/OpenGLDemo/photo/";
        File file = new File(folderPath);
        if (file == null || !file.isDirectory() || (!file.exists() && !file.mkdirs())) {
            if (mCallBack != null) {
                mCallBack.callFailed();
            }
            return;
        }
        String imagePath = folderPath + System.currentTimeMillis() + ".jpg";
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(imagePath);
            BufferedOutputStream bos = new BufferedOutputStream(output); // 存储的buffer
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (mCallBack != null) {
            mCallBack.callSuccess(imagePath);
        }
    }


    public void setCallBack(Camera2SurfaceView.CallBack callBack) {
        mCallBack = callBack;
    }

    interface CallBack {
        void callSuccess(String imagePath);

        void callFailed();
    }
}
