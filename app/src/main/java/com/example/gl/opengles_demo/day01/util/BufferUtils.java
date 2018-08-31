package com.example.gl.opengles_demo.day01.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

/**
 * author: ycl
 * date: 2018-08-31 10:08
 * desc: 缓冲区工具类
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
}
