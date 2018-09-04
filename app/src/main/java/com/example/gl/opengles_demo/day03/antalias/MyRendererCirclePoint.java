package com.example.gl.opengles_demo.day03.antalias;

import com.example.gl.opengles_demo.day01.util.BufferUtils;
import com.example.gl.opengles_demo.day03.blend.MyRendererBlend;

import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-04 17:01
 * desc: 平滑
 */
public class MyRendererCirclePoint extends MyRendererBlend {

    private float redAlpha = 0f ;
    private float blueAlpha = 45f ;
    private float yellowAlpha = -45f ;

    private float ratio ;
    private float theta = 0 ,x = 0 , y = 0;

    @Override
    public void onDrawChildFrame(GL10 gl) {
        gl.glShadeModel(GL10.GL_FLAT);

        //启用混合
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        //启用点平滑与线平滑(抗锯齿)
        gl.glEnable(GL10.GL_POINT_SMOOTH);
        gl.glHint(GL10.GL_POINT_SMOOTH_HINT, GL10.GL_FASTEST);// 暗示速度优先
        gl.glEnable(GL10.GL_LINE_SMOOTH);
        gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST); // 暗示质量优先



        float r = 0.8f ;
        gl.glPointSize(20f);

        //红色圆
        float redTheta = theta ;
        x = (float) ((float) r * Math.cos(redTheta));
        y = (float) ((float) r * Math.sin(redTheta));
        gl.glColor4f(1, 0, 0, 1);
        gl.glPushMatrix();
        gl.glRotatef(redAlpha, 0, 1, 0);
        gl.glRotatef(blueAlpha, 1, 0, 0);
        gl.glRotatef(yellowAlpha, 0, 0, 1);
        BufferUtils.drawCircle(gl, r);
        BufferUtils.drawPoint(gl,new float[]{x,y,0});
        gl.glPopMatrix();

        //蓝色圆
        redTheta = theta + 15 ;
        x = (float) ((float) r * Math.cos(redTheta));
        y = (float) ((float) r * Math.sin(redTheta));
        gl.glColor4f(0, 0, 1, 1);
        gl.glPushMatrix();
        gl.glRotatef(-90, 1, 0, 0);
        gl.glRotatef(blueAlpha, 0, 1, 0);
        gl.glRotatef(redAlpha, 1, 0, 0);
        gl.glRotatef(yellowAlpha, 0, 0, 1);
        BufferUtils.drawCircle(gl, r);
        BufferUtils.drawPoint(gl,new float[]{x,y,0});
        gl.glPopMatrix();


        //黄色圆
        redTheta = theta + 30 ;
        x = (float) ((float) r * Math.cos(redTheta));
        y = (float) ((float) r * Math.sin(redTheta));
        gl.glColor4f(1, 1, 0, 1);
        gl.glPushMatrix();
        gl.glRotatef(-90, 1, 0, 0);
        gl.glRotatef(yellowAlpha, 0, 1, 0);
        gl.glRotatef(redAlpha, 1, 0, 0);
        gl.glRotatef(blueAlpha, 0, 0, 1);
        BufferUtils.drawCircle(gl, r);
        BufferUtils.drawPoint(gl,new float[]{x,y,0});
        gl.glPopMatrix();
        theta = theta + 0.1f ;
        redAlpha = redAlpha + 1f ;
        blueAlpha = blueAlpha + 2f ;
        yellowAlpha = yellowAlpha + 3f ;

    }


}
