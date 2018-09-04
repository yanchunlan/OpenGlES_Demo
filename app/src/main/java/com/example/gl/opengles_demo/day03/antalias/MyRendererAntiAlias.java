package com.example.gl.opengles_demo.day03.antalias;

import com.example.gl.opengles_demo.day01.util.BufferUtils;
import com.example.gl.opengles_demo.day03.blend.MyRendererBlend;

import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-04 16:32
 * desc:
 */
public class MyRendererAntiAlias extends MyRendererBlend {

    @Override
    public void onDrawChildFrame(GL10 gl) {
        gl.glShadeModel(GL10.GL_FLAT);

        //启用混合
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        //启用点平滑与线平滑(抗锯齿)
        gl.glEnable(GL10.GL_POINT_SMOOTH);
        gl.glHint(GL10.GL_POINT_SMOOTH_HINT,GL10.GL_FASTEST);// 暗示速度优先
        gl.glEnable(GL10.GL_LINE_SMOOTH);
        gl.glHint(GL10.GL_LINE_SMOOTH_HINT,GL10.GL_NICEST); // 暗示质量优先


        float[] point = {
                0.5f,0.5f,0f
        };
        gl.glColor4f(0, 0, 1, 1);
        gl.glPointSize(15f);
        BufferUtils.drawPoint(gl, point);

        //画线
        float[] lines = {
                -.5f,-0.1f,1,
                -.2f,0.07f,1,
                .3f,-0.1f,1,
                .7f,0.3f,1,
        };
        gl.glColor4f(0, 1, 0, 1);
        //gl.glLineWidth(10f);
        BufferUtils.drawLineStrip(gl, lines);
    }
}
