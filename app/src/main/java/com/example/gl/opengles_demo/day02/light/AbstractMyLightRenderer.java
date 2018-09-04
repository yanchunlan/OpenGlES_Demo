package com.example.gl.opengles_demo.day02.light;

import com.example.gl.opengles_demo.day01.AbstractMyRenderer;

/**
 * author: ycl
 * date: 2018-09-03 15:19
 * desc:
 */
public abstract class AbstractMyLightRenderer extends AbstractMyRenderer {

    /*************************  光照  ***************************/
    // 光照
    public boolean enableLighting = false;

    //全局环境光
    public float global$ambient$r = 0.3f;
    public float global$ambient$g = 0.3f;
    public float global$ambient$b = 0.3f;
    public float global$ambient$a = 1f;

    //材料的环境光和散射光的反射率
    public float material$ambientanddiffuse$r = 0f ;
    public float material$ambientanddiffuse$g = 0f ;
    public float material$ambientanddiffuse$b = 0f ;
    public float material$ambientanddiffuse$a = 1f ;

    //是否启用颜色追踪
    public boolean enableColorMatrial = false ;
    public float color$r = 0.8f;
    public float color$g = 0.8f;
    public float color$b = 0.8f;
    public float color$a = 1f;

    //是否启用光源0
    public boolean enableLight0 = false ;

    //光源0-环境光
    public float light0$ambient$r = 0f;
    public float light0$ambient$g = 0f;
    public float light0$ambient$b = 0f;
    public float light0$ambient$a = 1f;
    //光源0-散射光
    public float light0$diffuse$r = 0f ;
    public float light0$diffuse$g = 0f ;
    public float light0$diffuse$b = 0f ;
    public float light0$diffuse$a = 1f ;
    //光源0-位置
    public float light0$pos$x = 0;
    public float light0$pos$y = 1f;
    public float light0$pos$z = 10f;
}
