package com.opengles.book.es2_0.egl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Looper;
import android.util.Log;

import com.opengles.book.es2_0.filter.AFilter;
import com.opengles.book.es2_0.filter.GrayFilter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;

/**
 * author: ycl
 * date: 2018-11-24 23:42
 * desc:  类似于GlSurfaceView的作用
 */
public class GLES20BackEnv {
    private static final String TAG = "GLES20BackEnv";
    private Context mContext;
    private String mImagePath;
    private int mWidth;
    private int mHeight;

    private EGLHelper mHelper;
    private String mThreadName;
    private AFilter mFilter;
    private Bitmap mBitmap;

    private GLES20BackEnv.CallBack mCallBack;

    public GLES20BackEnv(Context context, String imagePath) {
        this.mContext = context;
        this.mImagePath = imagePath;
        this.mThreadName = Looper.getMainLooper().getThread().getName();
        initEGLHelper(imagePath);
        initAFilter(context);
    }

    // 初始化 GL环境
    private void initEGLHelper(String imagePath) {
        this.mBitmap = BitmapFactory.decodeFile(imagePath);
        this.mWidth = mBitmap.getWidth();
        this.mHeight = mBitmap.getHeight();

        this.mHelper = new EGLHelper(mWidth, mHeight);
    }

    // 初始化 AFilter
    private void initAFilter(Context context) {
        this.mFilter = new GrayFilter(context.getResources());
        if (!Thread.currentThread().getName().equals(mThreadName)) {
            Log.e(TAG, "initAFilter:  currentThread name is not mThreadName");
            return;
        }

        // 类似于调用了onSurfaceCreated ， onSurfaceChanged
        mFilter.create();
        mFilter.setSize(mWidth, mHeight);
    }


    // 绘制
    public void draw() {
        if (mFilter == null) {
            return;
        }
        if (!Thread.currentThread().getName().equals(mThreadName)) {
            Log.e(TAG, "initAFilter:  currentThread name is not mThreadName");
            return;
        }
        // 类似于调用了 创建了texture 并调用了 onDrawFrame
        mFilter.setTextureId(createTextureId(mBitmap));
        mFilter.draw();
        convertToBitmap();
    }

    /**
     *  前面是根据图片转换为bitmap设置到texture里面去了，此处是从texture里面
     *  获取bitmap数据，并存在本地，并显示出来
     */
    private void convertToBitmap() {
        // 获取buffer
        IntBuffer ib = IntBuffer.allocate(mWidth * mHeight);
        mHelper.getGL().glReadPixels(0, 0, mWidth, mHeight, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, ib);
        int[] ia = ib.array();

        // 将倒置镜像反转图像转换为右侧正常的图像, 宽高置位 并把数据存在iat里面
        int[] iat = new int[mWidth * mHeight];
        for (int i = 0; i < mHeight; i++) {
            System.arraycopy(ia, i * mWidth, iat,
                    (mHeight - i - 1) * mWidth, mWidth);
        }

        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(iat)); // 从iat里面获取的数据，存在bitmap里面去
        save(bitmap);
    }

    private int createTextureId(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            int[] fTexture = new int[1];
            GLES20.glGenTextures(1, fTexture, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap, 0); // border 是边框
            return fTexture[0];
        }
        return 0;
    }

    public void destroy() {
        if (mHelper != null) mHelper.destroy();
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


    public void setCallBack(GLES20BackEnv.CallBack callBack) {
        mCallBack = callBack;
    }

    interface CallBack {
        void callSuccess(Bitmap bitmap);

        void callFailed();
    }
}
