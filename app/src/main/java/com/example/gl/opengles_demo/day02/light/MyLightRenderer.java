package com.example.gl.opengles_demo.day02.light;

import android.opengl.GLU;

import com.example.gl.opengles_demo.day01.util.BufferUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-02 18:33
 * desc: 光照
 */
public class MyLightRenderer extends AbstractMyLightRenderer {

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        gl.glEnableClientState(GL10.GL_DEPTH_TEST);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        //清除颜色缓冲区
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        //设置绘图颜色
        gl.glColor4f(1f, 0f, 0f, 1f);
        //单调模式
        gl.glShadeModel(GL10.GL_FLAT);

        //操作模型视图矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        //设置眼球的参数
        GLU.gluLookAt(gl, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f);

        // 旋转角度   绕x顺时针旋转90度
        gl.glRotatef(xrotate, 1f, 0, 0);
        gl.glRotatef(yrotate, 0, 1f, 0);
        /******************** 光照 *******************************/

        // 调用父类的打印方法
        System.out.printf("enableLighting:" + enableLighting);
        System.out.printf("global$ambient$r:" + global$ambient$r);
        System.out.printf("light0$ambient$r:" + light0$ambient$r);

        // 1.启用光照
        if (enableLighting) {
            gl.glEnable(GL10.GL_LIGHTING);
        } else {
            gl.glDisable(GL10.GL_LIGHTING);
        }

        // 2.设置全局环境光
        float[] global_ambient = {
                global$ambient$r,
                global$ambient$g,
                global$ambient$b,
                global$ambient$a,};
        gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, BufferUtils.arr2FloatBuffer(global_ambient));

        //设置材料的反射率（环境光和散射光）
        // ------------------ start ----------------------
        float[] matrial_ambient_diffuse = {
                material$ambientanddiffuse$r,
                material$ambientanddiffuse$g,
                material$ambientanddiffuse$b,
                material$ambientanddiffuse$a,
        };
        // 材料的面，正面，背面双面；设置环境光和材料反射光一致；
        gl.glLightfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, BufferUtils.arr2FloatBuffer(matrial_ambient_diffuse));
        // -------------------- end --------------------

        // 替换材料反射率，.启用颜色追踪 glColor4f
        if (enableColorMatrial) {
            gl.glEnable(GL10.GL_COLOR_MATERIAL);
        } else {
            gl.glDisable(GL10.GL_COLOR_MATERIAL);
        }
        //openGLES是固定的将环境光合散射光最终颜色,所以没有此方法
        //openGL:glColorMatrial();
        gl.glColor4f(color$r, color$g, color$b, color$a);

        // -----------  start  光源  ------------------
        if (enableLight0) {
            gl.glEnable(GL10.GL_LIGHT0);
        } else {
            gl.glDisable(GL10.GL_LIGHT0);
        }
        //环境光
        float[] light0_amient = {
                light0$ambient$r,
                light0$ambient$g,
                light0$ambient$b,
                light0$ambient$a
        };
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, BufferUtils.arr2FloatBuffer(light0_amient));
        //散射光
        float[] light0_diffuse = {
                light0$diffuse$r,
                light0$diffuse$g,
                light0$diffuse$b,
                light0$diffuse$a
        };
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, BufferUtils.arr2FloatBuffer(light0_diffuse));


        //镜面光 -- 非金属不需要，镜面
        /*   float[] light0_specular = { 1.0f, 1.0f, 1.0f, 1.0f}
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, BufferUtils.arr2FloatBuffer(light0_diffuse));
        */

        // 光源 位置，1表示光源就在此地,0表示无穷远
        float[] lightPos = {light0$pos$x, light0$pos$y, light0$pos$z, 1f};
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, BufferUtils.arr2FloatBuffer(lightPos));
        // -----------  end  光源  ------------------

        BufferUtils.drawSphere(gl, 0.6f, 8, 8);

    }

    @Override
    public void onDrawChildFrame(GL10 gl) {
    }

    @Override
    public String toString() {
        return "MyLightRenderer{" +
                "enableLighting=" + enableLighting +
                ", global$ambient$r=" + global$ambient$r +
                ", global$ambient$g=" + global$ambient$g +
                ", global$ambient$b=" + global$ambient$b +
                ", global$ambient$a=" + global$ambient$a +
                ", material$ambientanddiffuse$r=" + material$ambientanddiffuse$r +
                ", material$ambientanddiffuse$g=" + material$ambientanddiffuse$g +
                ", material$ambientanddiffuse$b=" + material$ambientanddiffuse$b +
                ", material$ambientanddiffuse$a=" + material$ambientanddiffuse$a +
                ", enableColorMatrial=" + enableColorMatrial +
                ", color$r=" + color$r +
                ", color$g=" + color$g +
                ", color$b=" + color$b +
                ", color$a=" + color$a +
                ", enableLight0=" + enableLight0 +
                ", light0$ambient$r=" + light0$ambient$r +
                ", light0$ambient$g=" + light0$ambient$g +
                ", light0$ambient$b=" + light0$ambient$b +
                ", light0$ambient$a=" + light0$ambient$a +
                ", light0$diffuse$r=" + light0$diffuse$r +
                ", light0$diffuse$g=" + light0$diffuse$g +
                ", light0$diffuse$b=" + light0$diffuse$b +
                ", light0$diffuse$a=" + light0$diffuse$a +
                ", light0$pos$x=" + light0$pos$x +
                ", light0$pos$y=" + light0$pos$y +
                ", light0$pos$z=" + light0$pos$z +
                '}';
    }
}
