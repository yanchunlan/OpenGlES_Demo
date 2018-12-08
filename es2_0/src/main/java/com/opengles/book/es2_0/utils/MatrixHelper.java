package com.opengles.book.es2_0.utils;

/**
 * Created by aiya on 2017/5/22.
 */

public class MatrixHelper {

    // (mProjectMatrix, 0, 45, ratio, 1, 300)
    public static void perspectiveM(float[] m, int offset,
        float fovy, float aspect, float zNear, float zFar) {
        float f = 1.0f / (float) Math.tan(fovy * (Math.PI / 360.0)); // 旋转角度宽高比 的倒数
        float rangeReciprocal = 1.0f / (zNear - zFar);  // view与真实距离的比例

        m[offset + 0] = f / aspect;    // x=1/ ratio*viewWH
        m[offset + 1] = 0.0f;
        m[offset + 2] = 0.0f;
        m[offset + 3] = 0.0f;

        m[offset + 4] = 0.0f;
        m[offset + 5] = f;          // y=1/viewWH
        m[offset + 6] = 0.0f;
        m[offset + 7] = 0.0f;

        m[offset + 8] = 0.0f;
        m[offset + 9] = 0.0f;
        m[offset + 10] = (zFar + zNear) * rangeReciprocal;  // ???不知了
        m[offset + 11] = -1.0f;

        m[offset + 12] = 0.0f;
        m[offset + 13] = 0.0f;
        m[offset + 14] = 2.0f * zFar * zNear * rangeReciprocal;
        m[offset + 15] = 0.0f;
    }

}
