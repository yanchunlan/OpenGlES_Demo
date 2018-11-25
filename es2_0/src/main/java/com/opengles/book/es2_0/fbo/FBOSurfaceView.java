package com.opengles.book.es2_0.fbo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.util.AttributeSet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * author: ycl
 * date: 2018-11-24 22:42
 * desc:
 */
public class FBOSurfaceView extends GLSurfaceView implements FBORenderer.CallBack {
    private FBORenderer mFBORenderer;
    private CallBack mCallBack;
    private int mBmpWidth, mBmpHeight;
    private String mImagePath;


    public FBOSurfaceView(Context context) {
        this(context, null);
    }

    public FBOSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        mFBORenderer = new FBORenderer(context.getResources());
        mFBORenderer.setCallBack(this);

        setEGLContextClientVersion(2);
        setRenderer(mFBORenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    public void setImagePath(String imagePath) {
        this.mImagePath = imagePath;
        if (mFBORenderer != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            mBmpWidth = bitmap.getWidth();
            mBmpHeight = bitmap.getHeight();
            mFBORenderer.setBitmap(bitmap);
            requestRender();
        }
    }

    @Override
    public void call(final ByteBuffer buffer) {
        // onDraw 里面返回
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(mBmpWidth, mBmpHeight, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer); // buffer 转化为bitmap
                save(bitmap);
                buffer.clear();
            }
        });
    }

    // 保存bitmap成为本地文件
    private void save(Bitmap bitmap) {
        String folderPath = mImagePath.substring(0, mImagePath.lastIndexOf("/") + 1);
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
            mCallBack.callSuccess(bitmap);
        }
    }


    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    interface CallBack {
        void callSuccess(Bitmap bitmap);

        void callFailed();
    }
}

