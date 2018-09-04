package com.example.gl.opengles_demo.day03.light;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;

/**
 * author: ycl
 * date: 2018-09-03 15:52
 * desc:
 */
public abstract class AbstractMyLightRenderer1 extends AbstractMyRenderer {

    //是否启用光照
    public boolean enable_lighting = true;

    //启用颜色追踪
    public boolean enable_color_material = true;

    //重放法线规范化
    public boolean enable_rescale_normals = true;

    //启用光源0
    public boolean enable_light0 = true;


    //全局环境光
    public float global_ambient_r = 0.5f;
    public float global_ambient_g = 0.5f;
    public float global_ambient_b = 0.5f;
    public float global_ambient_a = 1f;

    //材料的环境光和散射光的反射率(一般设为相同的值)
    public float material_ambient_and_diffuse_r = 0.75f;
    public float material_ambient_and_diffuse_g = 0.75f;
    public float material_ambient_and_diffuse_b = 0.75f;
    public float material_ambient_and_diffuse_a = 1f;

    //材料的镜面光反射率
    public float material_specular_r = 1f;
    public float material_specular_g = 1f;
    public float material_specular_b = 1f;
    public float material_specular_a = 1f;

    //颜色设置
    public float color_r = 0.75f;
    public float color_g = 0.75f;
    public float color_b = 0.75f;
    public float color_a = 1f;

    //光源0的环境光成分
    public float light0_ambient_r = 0.3f;
    public float light0_ambient_g = 0.3f;
    public float light0_ambient_b = 0.3f;
    public float light0_ambient_a = 1f;

    //光源0的散射光成分
    public float light0_diffuse_r = 0.7f;
    public float light0_diffuse_g = 0.7f;
    public float light0_diffuse_b = 0.7f;
    public float light0_diffuse_a = 1f;

    //光源0的镜面光成分,产生镜面亮点
    public float light0_specular_r = 1f;
    public float light0_specular_g = 1f;
    public float light0_specular_b = 1f;
    public float light0_specular_a = 1f;

    //light0的位置
    public float light0_pos_x = 0;
    public float light0_pos_y = 3;
    public float light0_pos_z = 8;

}
