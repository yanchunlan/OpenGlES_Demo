package com.example.gl.opengles_demo.day02.shere;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-01 16:51
 * desc: 球
 */
public class MySphereRenderer extends AbstractMyRenderer {
    @Override
    public void onDrawChildFrame(GL10 gl) {
        // 计算球体坐标  按照角度分层
        float R = 0.5f; // 球半径
        int stack = 6; // 水平层数
        float stackStep = (float) (Math.PI / stack );// 单位角度值

        int slice = 8;// 8刀16层
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
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, coordsList.size() / 3); // 直线代
    }
}
