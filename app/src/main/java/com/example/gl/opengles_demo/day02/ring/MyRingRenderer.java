package com.example.gl.opengles_demo.day02.ring;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-01 22:35
 * desc: 圆环
 */
public class MyRingRenderer extends AbstractMyRenderer {
    @Override
    public void onDrawChildFrame(GL10 gl) {
        List<Float> coordsList = new ArrayList<Float>();
        float rinner = 0.2f;//内环半径
        float ring = 0.3f;//环半径

        int count = 20;
        float alphaStep = (float) (Math.PI * 2 / count);
        float alpha = 0;
        float x0, x1, y0, y1, z0, z1;

        int countb = 20;
        float betaStep = (float) (Math.PI * 2 / countb);
        float beta = 0;

        // 因为i +1 了所以只需要< ，j 没有+1，要取完所有则需要等于，取到最后一个
        for (int i = 0; i <count; i++) {
            alpha = i * alphaStep;
            for (int j = 0; j <= countb; j++) {
                beta = j * betaStep;
                x0 = (float) (Math.cos(alpha) * (rinner + ring + ring * Math.cos(beta)));
                y0 = (float) (Math.sin(alpha) * (rinner + ring + ring * Math.cos(beta)));
                z0 = -(float) (ring*Math.sin(beta));

                x1 = (float) (Math.cos(alpha+alphaStep) * (rinner + ring + ring * Math.cos(beta)));
                y1 = (float) (Math.sin(alpha+alphaStep) * (rinner + ring + ring * Math.cos(beta)));
                z1 = -(float) (ring*Math.sin(beta));
                coordsList.add(x0);
                coordsList.add(y0);
                coordsList.add(z0);
                coordsList.add(x1);
                coordsList.add(y1);
                coordsList.add(z1);
            }
        }
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtils.list2FloatBuffer(coordsList));
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, coordsList.size() / 3);
    }
}
