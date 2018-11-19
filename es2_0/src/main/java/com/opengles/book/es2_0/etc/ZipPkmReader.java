package com.opengles.book.es2_0.etc;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.ETC1;
import android.opengl.ETC1Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * author: ycl
 * date: 2018-11-19 22:07
 * desc: 读取zip文件并生成 ETC1Texture
 */
public class ZipPkmReader {

    private String mPath;
    private AssetManager mAssetManager;

    private ZipInputStream mZipInputStream;
    private ZipEntry mZipEntry;

    private ByteBuffer mHeaderBuffer;


    public ZipPkmReader(Context context) {
        mAssetManager = context.getAssets();
    }


    public void setPath(String path) {
        mPath = path;
    }

    public boolean open() {
        if (mPath == null) return false;
        try {
            if (mPath.startsWith("assets/")) {
                InputStream in = mAssetManager.open(mPath);
                mZipInputStream = new ZipInputStream(in);
            } else {
                File file = new File(mPath);
                mZipInputStream = new ZipInputStream(new FileInputStream(file));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        if (mZipInputStream != null) {
            try {
                mZipInputStream.closeEntry();
                mZipInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mHeaderBuffer != null) {
            mHeaderBuffer.clear();
            mHeaderBuffer = null;
        }
    }

    private boolean hasElements() {
        if (mZipInputStream != null) {
            try {
                mZipEntry = mZipInputStream.getNextEntry();
                if (mZipEntry != null) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public InputStream getNextStream() {
        if (hasElements()) {
            return mZipInputStream;
        }
        return null;
    }

    public ETC1Util.ETC1Texture getNextTexture() {
        if (hasElements()) {
            try {
                return createTexture(mZipInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    // 因为原生的 createTexture 方法有点小坑，所有需要修改
    public ETC1Util.ETC1Texture createTexture(InputStream input) throws IOException {
        int width = 0;
        int height = 0;
        byte[] ioBuffer = new byte[4096];
        {
            if (input.read(ioBuffer, 0, ETC1.ETC_PKM_HEADER_SIZE) != ETC1.ETC_PKM_HEADER_SIZE) {
                throw new IOException("Unable to read PKM file header.");
            }
            // 修改1 ，headBuffer只初始化一次
            if (mHeaderBuffer == null) {
                mHeaderBuffer = ByteBuffer.allocateDirect(ETC1.ETC_PKM_HEADER_SIZE)
                        .order(ByteOrder.nativeOrder());
            }
            mHeaderBuffer.put(ioBuffer, 0, ETC1.ETC_PKM_HEADER_SIZE).position(0);
            if (!ETC1.isValid(mHeaderBuffer)) {
                throw new IOException("Not a PKM file.");
            }
            width = ETC1.getWidth(mHeaderBuffer);
            height = ETC1.getHeight(mHeaderBuffer);
        }
        int encodedSize = ETC1.getEncodedDataSize(width, height);
        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(encodedSize).order(ByteOrder.nativeOrder());
        /*for (int i = 0; i < encodedSize; ) {
            int chunkSize = Math.min(ioBuffer.length, encodedSize - i);
            if (input.read(ioBuffer, 0, chunkSize) != chunkSize) {
                throw new IOException("Unable to read PKM file data.");
            }
            dataBuffer.put(ioBuffer, 0, chunkSize);
            i += chunkSize;
        }*/
        int len;
        while ((len = input.read(ioBuffer)) != -1) {
            dataBuffer.put(ioBuffer, 0, len);
        }
        dataBuffer.position(0);
        return new ETC1Util.ETC1Texture(width, height, dataBuffer);
    }

}
