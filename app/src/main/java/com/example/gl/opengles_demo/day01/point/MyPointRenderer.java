package com.example.gl.opengles_demo.day01.point;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-08-30 23:22
 * desc: 点渲染器,绘制螺旋线
 */
public class MyPointRenderer extends AbstractMyRenderer {

    @Override
    public void onDrawChildFrame(GL10 gl) {
        // 计算点坐标
        float r = 0.5f; // 半径
        List<Float> coordsList = new ArrayList<>();
        float x = 0f, y = 0f, z = 1f, zStep = 0.01f;
        // 3圈6pi , z初始值为1，慢慢减小
        for (float alpha = 0f; alpha < Math.PI * 6; alpha = (float) (alpha + Math.PI / 16)) {
            x = (float) (r * Math.cos(alpha));
            y = (float) (r * Math.sin(alpha));
            z = z - zStep;
            coordsList.add(x);
            coordsList.add(y);
            coordsList.add(z);
        }

        // 转换点成为缓冲区数据
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtils.list2ByteBuffer(coordsList));
        gl.glDrawArrays(GL10.GL_POINTS, 0, coordsList.size() / 3);
    }
}
