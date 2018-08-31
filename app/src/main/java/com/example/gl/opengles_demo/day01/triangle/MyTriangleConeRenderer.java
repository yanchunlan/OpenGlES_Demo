package com.example.gl.opengles_demo.day01.triangle;

import android.opengl.GLU;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;
import com.example.gl.opengles_demo.day01.util.BufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-08-31 14:46
 * desc: 棱锥,正方形
 */
public class MyTriangleConeRenderer extends AbstractMyRenderer {


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        //启用眼色缓冲区
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        //清除颜色缓冲区和深度缓冲区
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
        //设置绘图颜色
        gl.glColor4f(1f, 0f, 0f, 1f);

        //启用深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST);

        // 启动表面剔除
        gl.glEnable(GL10.GL_CULL_FACE);
        //指定前面()
        //ccw:counter clock wise-->逆时针
        //cw:clock wise--> 顺时针
        gl.glFrontFace(GL10.GL_CCW);
        //剔除背面
        gl.glCullFace(GL10.GL_BACK);  // 针对表面剔除背面

        //GL10.GL_SMOOTH:平滑着色(默认)
        //GL10.GL_FLAT:单调模式
        gl.glShadeModel(GL10.GL_FLAT); // 着色模式


        //操作模型视图矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        //设置眼球的参数
        GLU.gluLookAt(gl, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f);

        // 旋转角度   绕x顺时针旋转90度
        gl.glRotatef(xrotate, 1f, 0, 0);
        gl.glRotatef(yrotate, 0, 1f, 0);


        // 计算点坐标
        float r = 0.5f; // 半径
        float x = 0f, y = 0f, z = -0.5f;


        /******************** 锥面 ************************/
        // 顶点坐标集合
        List<Float> coordsList = new ArrayList<>();
        // 锥顶点
        coordsList.add(0f);
        coordsList.add(0f);
        coordsList.add(1f);

        // 顶点颜色集合
        List<Float> colorList = new ArrayList<>();
        colorList.add(1f);//r
        colorList.add(0f);//g
        colorList.add(0f);//b
        colorList.add(1f);//a
        /******************** 锥底 ************************/
        //锥底坐标
        List<Float> coordBottomsList = new ArrayList<>();
        coordBottomsList.add(0f);
        coordBottomsList.add(0f);
        coordBottomsList.add(-0.5f);

        boolean flag = false;

        for (float alpha = 0f; alpha < Math.PI * 6; alpha = (float) (alpha + Math.PI / 8)) {
            x = (float) (r * Math.cos(alpha));
            y = (float) (r * Math.sin(alpha));
            coordsList.add(x);
            coordsList.add(y);
            coordsList.add(z);

            //锥底坐标
            coordBottomsList.add(x);
            coordBottomsList.add(y);
            coordBottomsList.add(z);

            // 点颜色值
            if (flag = !flag) {
                //黄色
                colorList.add(1f);
                colorList.add(1f);
                colorList.add(0f);
                colorList.add(1f);
            } else {
                //红色
                colorList.add(1f);
                colorList.add(0f);
                colorList.add(0f);
                colorList.add(1f);
            }
        }

        //点颜色值
        if(flag = !flag){
            //黄色
            colorList.add(1f);
            colorList.add(1f);
            colorList.add(0f);
            colorList.add(1f);
        }
        else{
            //红色
            colorList.add(1f);
            colorList.add(0f);
            colorList.add(0f);
            colorList.add(1f);
        }

        //颜色缓冲区
        ByteBuffer colorBuffer = BufferUtils.list2ByteBuffer(colorList);
        //绘制锥面
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtils.list2ByteBuffer(coordsList));
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, coordsList.size() / 3);


        //剔除正面
        gl.glCullFace(GL10.GL_FRONT); // 针对底剔除正面
        //绘制锥底
        colorBuffer.position(4); // 为了颜色相间，锥底需要移动4个字节
        gl.glColorPointer(4, GL10.GL_FLOAT, 0,colorBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtils.list2ByteBuffer(coordBottomsList));
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, coordBottomsList.size() / 3);
    }

    @Override
    public void onDrawChildFrame(GL10 gl) {
    }
}
