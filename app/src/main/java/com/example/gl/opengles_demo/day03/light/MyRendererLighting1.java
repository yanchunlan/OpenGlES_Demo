package com.example.gl.opengles_demo.day03.light;

import android.opengl.GLU;

import com.example.gl.opengles_demo.day01.util.BufferUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-09-03 15:42
 * desc: 光照（复习）
 */
public class MyRendererLighting1 extends AbstractMyLightRenderer1 {

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
//        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_DEPTH_TEST);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清除颜色缓冲区
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
        //设置绘图颜色
        gl.glColor4f(1f, 0f, 0f, 1f);

        //操作模型视图矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        //设置眼球的参数
        GLU.gluLookAt(gl, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f);

        // 旋转角度   绕x顺时针旋转90度
        gl.glRotatef(xrotate, 1f, 0, 0);
        gl.glRotatef(yrotate, 0, 1f, 0);

        onDrawChildFrame(gl);
    }




    @Override
    public void onDrawChildFrame(GL10 gl) {
        gl.glShadeModel(GL10.GL_FLAT);

        /**************** 光照 **********************/
        if (enable_lighting) {
            gl.glEnable(GL10.GL_LIGHTING);
        } else {
            gl.glDisable(GL10.GL_LIGHTING);
        }

        //全局光环境
        float[] global_ambient = {
                global_ambient_r,
                global_ambient_g,
                global_ambient_b,
                global_ambient_a,
        };
        gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, BufferUtils.arr2FloatBuffer(global_ambient));

        /***************************************材料***********************************************/
        //设置材料的反射率
        float[] material_ambient_diffuse = {
                material_ambient_and_diffuse_r,
                material_ambient_and_diffuse_g,
                material_ambient_and_diffuse_b,
                material_ambient_and_diffuse_a
        };
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, BufferUtils.arr2FloatBuffer(material_ambient_diffuse));


        //是否启用颜色追踪,openGLES不支持颜色跟踪的具体情况设置 ,默认环境，散光一致，只需要设置颜色即可
        if (enable_color_material) {
            gl.glEnable(GL10.GL_COLOR_MATERIAL);
        } else {
            gl.glDisable(GL10.GL_COLOR_MATERIAL);
        }
        gl.glColor4f(color_r, color_g, color_b, color_a);

        //设置材料的镜面光发射率
        float[] material_specular = {
                material_specular_r,
                material_specular_g,
                material_specular_b,
                material_specular_a,
        };
        //GL_FRONT_AND_BACK:前面/后面
        gl.glMaterialfv(GL10.GL_BACK, GL10.GL_SPECULAR, BufferUtils.arr2FloatBuffer(material_specular));
        //设置材料的镜面指数(亮度,1-128)
        gl.glMaterialx(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 128);
        /***************************************光源***********************************************/
        //重放法线规范化
        if (enable_rescale_normals) {
            gl.glEnable(GL10.GL_RESCALE_NORMAL);
        } else {
            gl.glDisable(GL10.GL_RESCALE_NORMAL);
        }

        //使用启用光源0
        if (enable_light0) {
            gl.glEnable(GL10.GL_LIGHT0);
        } else {
            gl.glDisable(GL10.GL_LIGHT0);
        }
        //设置光源0的环境光
        float[] light0_ambient = {
                light0_ambient_r,
                light0_ambient_g,
                light0_ambient_b,
                light0_ambient_a,
        };
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, BufferUtils.arr2FloatBuffer(light0_ambient));
        //设置光源0的散射光
        float[] light0_diffuse = {
                light0_diffuse_r,
                light0_diffuse_g,
                light0_diffuse_b,
                light0_diffuse_a,
        };
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, BufferUtils.arr2FloatBuffer(light0_diffuse));
        //光源0-镜面光成分
        float[] light0_specular = {
                light0_specular_r,
                light0_specular_g,
                light0_specular_b,
                light0_specular_a,
        };
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, BufferUtils.arr2FloatBuffer(light0_specular));
        //设置光源位置,1.0表示光源就在该位置,如果为0,表示位于无穷远,得到平行光.
        float[] light0_pos = {
                light0_pos_x,
                light0_pos_y,
                light0_pos_z,
                1f
        };
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, BufferUtils.arr2FloatBuffer(light0_pos));
        //设置切角 15f
        gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_CUTOFF, 15f);

        BufferUtils.drawSphere(gl, 0.6f, 8, 8);
    }
}
