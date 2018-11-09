package com.opengles.book.es2_0.utils;

import android.opengl.Matrix;

import java.util.Arrays;
import java.util.Stack;

/**
 * @Author: ycl
 * @Date: 2018/11/9 10:53
 * @Desc: 矩阵变换工具类
 */
public class VaryTools {
    private float[] mMatrixCamera = new float[16];  //相机矩阵
    private float[] mMatrixProjection = new float[16]; //投影矩阵
    private float[] mMatrixCurrent = { // 原始矩阵
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };
    private Stack<float[]> mStack; //变换矩阵堆栈

    public VaryTools() {
        mStack = new Stack<>();
    }

    //保护现场
    public void pushMatrix() {
        // 每次push之后就相当于新建了一个变换矩阵，当作用完就删除变换矩阵
        mStack.push(Arrays.copyOf(mMatrixCurrent, 16));
    }

    //恢复现场
    public void popMatrix() {
        mMatrixCurrent = mStack.pop();
    }

    public void clearStack() {
        mStack.clear();
    }

    //平移变换
    public void translate(float x, float y, float z) {
        Matrix.translateM(mMatrixCurrent, 0, x, y, z);
    }

    //旋转变换
    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(mMatrixCurrent, 0, angle, x, y, z);
    }

    //缩放变换
    public void scale(float x, float y, float z) {
        Matrix.scaleM(mMatrixCurrent, 0, x, y, z);
    }

    //设置相机
    public void setLookAtM(float ex, float ey, float ez, float cx, float cy, float cz, float ux, float uy, float uz) {
        Matrix.setLookAtM(mMatrixCamera, 0, ex, ey, ez, cx, cy, cz, ux, uy, uz);
    }

    public void frustum(float left, float right, float bottom, float top, float near, float far) {
        Matrix.frustumM(mMatrixProjection, 0, left, right, bottom, top, near, far);
    }

    public void ortho(float left, float right, float bottom, float top, float near, float far) {
        Matrix.orthoM(mMatrixProjection, 0, left, right, bottom, top, near, far);
    }

    public float[] getFinalMatrix() {
        float[] mvpMatrix = new float[16];
//        // 以前的逻辑是 = 拼接头提*相机
//        Matrix.multiplyMM(mvpMatrix, 0, mMatrixCamera, 0, mMatrixCurrent, 0);
        // 现在的逻辑是  01 = 相机*变换 ， 01=凭借头提* 01
        Matrix.multiplyMM(mvpMatrix, 0, mMatrixCamera, 0, mMatrixCurrent, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mMatrixProjection, 0, mvpMatrix, 0);
        return mvpMatrix;
    }
}