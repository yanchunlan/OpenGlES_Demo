package com.example.gl.opengles_demo.day01.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

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
     * 绘制矩形
     */
    public static void drawRect(GL10 gl, float[] vertexCoords) {
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, arr2FloatBuffer(vertexCoords));
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertexCoords.length / 3);
    }

    /**
     * 绘制点
     */
    public static void drawPoint(GL10 gl, float[] coords) {
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, arr2FloatBuffer(coords));
        gl.glDrawArrays(GL10.GL_POINTS, 0, coords.length / 3);
    }

    /**
     * 绘制线
     */
    public static void drawLineStrip(GL10 gl, float[] coords) {
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, arr2FloatBuffer(coords));
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, coords.length / 3);
    }

    /**
     * 绘制园
     */
    public static void drawCircle(GL10 gl, float r) {
        List<Float> coords = new ArrayList<>();
        float x = 0, y = 0, z = 0;
        for (float angle = 0; angle < Math.PI * 2; angle = (float) (angle + Math.PI / 16)) {
            x = (float) (r * Math.cos(angle));
            y = (float) (r * Math.sin(angle));
            coords.add(x);
            coords.add(y);
            coords.add(z);
        }

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, list2FloatBuffer(coords));
        gl.glDrawArrays(GL10.GL_LINES, 0, coords.size() / 3);
    }


    /**
     * 绘制球体
     */
    public static void drawSphere(GL10 gl, float R, int stack, int slice) {
        // 计算球体坐标  按照角度分层
//        float R = 0.5f; // 球半径
//        int stack = 6; // 水平层数
//        int slice = 8;// 8刀16层
        float stackStep = (float) (Math.PI / stack);// 单位角度值
        float sliceStep = (float) ((Math.PI * 2) / (slice * 2));// 水平圆递增的角度

        float r0, r1, x0, x1, y0, y1, z0, z1;
        float alpha0 = 0, alpha1 = 0;
        float beta = 0;
        List<Float> coordsList = new ArrayList<Float>();

        // -PI/2  -> PI/2 之间变换
        for (int i = 0; i < stack; i++) {
            alpha0 = (float) (-Math.PI / 2 + i * stackStep);
            alpha1 = (float) (-Math.PI / 2 + (i + 1) * stackStep);
            // 此处因为是在-PI/2间，注意角度变换 ，alpha是相反得那个角度
            y0 = (float) (R * Math.sin(alpha0));
            r0 = (float) (R * Math.cos(alpha0));
            y1 = (float) (R * Math.sin(alpha1));
            r1 = (float) (R * Math.cos(alpha1));
            //循环每一层圆 所以*2 ，因为首位相接，所以<= , 从中心切，0-2PI
            for (int j = 0; j <= slice * 2; j++) {
                beta = j * sliceStep;
                // 按照笛卡尔坐标 z是相反得
                x0 = (float) (r0 * Math.cos(beta));
                z0 = -(float) (r0 * Math.sin(beta));
                x1 = (float) (r1 * Math.cos(beta));
                z1 = -(float) (r1 * Math.sin(beta));
                coordsList.add(x0);
                coordsList.add(y0);
                coordsList.add(z0);
                coordsList.add(x1);
                coordsList.add(y1);
                coordsList.add(z1);
            }
        }

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtils.list2FloatBuffer(coordsList));
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, coordsList.size() / 3); // 直线代
    }

}
