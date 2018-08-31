package com.example.gl.opengles_demo.day01.triangle;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-08-30 18:05
 * desc:   自定义渲染器  --  三角形
 */
public class MyRenderer implements GLSurfaceView.Renderer {

    private float ratio;

    // 表层创建
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


        // 设置清屏色
        gl.glClearColor(0f, 0f, 0f, 1f);
        // 启用顶点缓冲区.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    }

    // 表层size改变
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置视口，输出画面的区域
        gl.glViewport(0, 0, width, height);
        ratio = (float) width / (float) height;

        // 矩阵模式：投影矩阵，openGl基于状态机
        gl.glMatrixMode(GL10.GL_PROJECTION);

        //加载单位矩阵
        gl.glLoadIdentity();

        // 平截头体（假设x为ratio单位，则y为1，向正方形就正，负方向为负,近平面距离3，远平面距离7）
        gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);
//        gl.glFrustumf(-1f, 1f, -ratio, ratio, 3, 7);

    }

    //绘图
    @Override
    public void onDrawFrame(GL10 gl) {
        // 因为此处需要设置眼球参数，所以需要更改模型矩阵
        //前三个参数表示的是脑袋的位置，中间三个参数是人眼的朝向，后三个位置表示的是脑袋朝向的方向。
        // eyex,eyey,eyez: 眼球的坐标  平截头体在中心的时候，坐标在005
        // centerx,centery,centerz:镜头朝向  z<5都可以，随便去000
        // upx,upx,upx: 指定眼球向上的向量  010

        //清除颜色缓存区
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // 模型视图矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity(); //加载单位矩阵
        GLU.gluLookAt(gl, 0, 0, 5, 0, 0, 0, 0, 1, 0);

        // 要贴满，必须要z是最大
        float[] coords = {
                0f, 1, 2f,
                -ratio, -1, 2f,
                ratio, -1, 2f,
        };
       /* float[] coords = {
                0f, ratio, 2f,
                -1f, -ratio, 2f,
               1f, -ratio, 2f,
        };*/


        // 分配字节缓存区空间,存放顶点坐标数据 每个浮点数4字节
        ByteBuffer ibb = ByteBuffer.allocateDirect(coords.length * 4);
        //设置的顺序(本地顺序)
        ibb.order(ByteOrder.nativeOrder());
        //放置顶点坐标数组
        FloatBuffer fbb = ibb.asFloatBuffer();
        fbb.put(coords);
        //定位指针的位置,从该位置开始读取顶点数据
        ibb.position(0);


        //设置绘图颜色,红色
        gl.glColor4f(1f, 0f, 0f, 1f);


        // 放置定点坐标 与缓冲器一致
        //3:3维点,使用三个坐标值表示一个点,3维坐标
        //type:每个点的数据类型 float
        //stride:0,跨度. 一般情况下写0系统会自动识别。识别方式为size*sizeof(数组定义时报类型)
        //ibb:指定顶点缓冲区
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, ibb);

        //绘制三角形
        //0:起始点:
        //3:绘制点的数量
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
    }
}

