package com.opengles.book.es2_0_test2.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

/**
 * author: ycl
 * date: 2018-10-29 23:37
 * desc:
 */
public class BufferUtils {
    /**
     * 将浮点数组转换成字节缓冲区
     */
    public static ByteBuffer arr2ByteBuffer(float[] arr) {
        ByteBuffer ibb = ByteBuffer.allocateDirect(arr.length * 4);
        ibb.order(ByteOrder.nativeOrder());
        FloatBuffer fbb = ibb.asFloatBuffer();
        fbb.put(arr);
        fbb.position(0);
        return ibb;
    }

    /**
     * 将byte数组转换成字节缓冲区
     */
    public static ByteBuffer arr2ByteBuffer(byte[] arr) {
        ByteBuffer ibb = ByteBuffer.allocateDirect(arr.length);
        ibb.order(ByteOrder.nativeOrder());
        ibb.put(arr);
        ibb.position(0);
        return ibb;
    }

    /**
     * 将list转换成字节缓冲区
     */
    public static ByteBuffer list2ByteBuffer(List<Float> arr) {
        ByteBuffer ibb = ByteBuffer.allocateDirect(arr.size() * 4);
        ibb.order(ByteOrder.nativeOrder());
        FloatBuffer fbb = ibb.asFloatBuffer();
        for (Float f : arr) {
            fbb.put(f);
        }
        fbb.position(0);
        return ibb;
    }

    /**
     * 将list转换成浮点缓冲区
     */
    public static FloatBuffer list2FloatBuffer(List<Float> arr) {
        ByteBuffer ibb = ByteBuffer.allocateDirect(arr.size() * 4);
        ibb.order(ByteOrder.nativeOrder());
        FloatBuffer fbb = ibb.asFloatBuffer();
        for (Float f : arr) {
            fbb.put(f);
        }
        fbb.position(0);
        return fbb;
    }

    /**
     * 将float转换成浮点缓冲区
     */
    public static FloatBuffer arr2FloatBuffer(float[] arr) {
        ByteBuffer ibb = ByteBuffer.allocateDirect(arr.length * 4);
        ibb.order(ByteOrder.nativeOrder());
        FloatBuffer fbb = ibb.asFloatBuffer();
        fbb.put(arr);
        fbb.position(0);
        return fbb;
    }

   /**
     * 将short转换成short缓冲区
     */
    public static ShortBuffer arr2ShortBuffer(short[] arr) {
        ByteBuffer ibb = ByteBuffer.allocateDirect(arr.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        ShortBuffer fbb = ibb.asShortBuffer();
        fbb.put(arr);
        fbb.position(0);
        return fbb;
    }

}
