package com.opengles.book.es2_0.image.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.opengles.book.es2_0.utils.BufferUtils;
import com.opengles.book.es2_0.utils.ShaderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-05 0:04
 * desc:
 */
public abstract class AFilter implements GLSurfaceView.Renderer {
    private static final String TAG = "AFilter";
    private Context context;
 /*   private String vertexShaderCodes = "attribute vec4 vPosition;" +
            "uniform mat4 vMatrix;" +
            "attribute vec2 vCoordinate;" +
            "varying vec2 aCoordinate;" +
            "varying vec4 aPos;" +
            "varying vec4 gPosition;" +
            "void main(){" +
            "gl_Position=vMatrix*vPosition;" +
            "aPos=vPosition;" +
            "aCoordinate=vCoordinate;" +
            "gPosition=vMatrix*vPosition;" +
            "}";
    private String fragmentShaderCodes =
            "precision mediump float;" +
                    "uniform sampler2D vTexture;" +
                    "uniform int vChangeType;" +
                    "uniform vec3 vChangeColor;" +
                    "uniform int vIsHalf;" +
                    "uniform float uXY;" +
                    "" +
                    "varying vec2 aCoordinate;" +
                    "varying vec4 aPos;" +
                    "varying vec4 gPosition;" +
                    "\n" +
                    "void modifyColor(vec4 color){\n" +
                    "    color.r=max(min(color.r,1.0),0.0);\n" +
                    "    color.g=max(min(color.g,1.0),0.0);\n" +
                    "    color.b=max(min(color.b,1.0),0.0);\n" +
                    "    color.a=max(min(color.a,1.0),0.0);\n" +
                    "}\n" +
                    "\n" +
                    "void main(){\n" +
                    "    vec4 nColor=texture2D(vTexture,aCoordinate);\n" +
                    "    if(aPos.x>0.0||vIsHalf==0){\n" +
                    "        if(vChangeType==1){\n" +
                    "            float c=nColor.r*vChangeColor.r+nColor.g*vChangeColor.g+nColor.b*vChangeColor.b;\n" +
                    "            gl_FragColor=vec4(c,c,c,nColor.a);\n" +
                    "        }else if(vChangeType==2){\n" +
                    "            vec4 deltaColor=nColor+vec4(vChangeColor,0.0);\n" +
                    "            modifyColor(deltaColor);\n" +
                    "            gl_FragColor=deltaColor;\n" +
                    "        }else if(vChangeType==3){\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.r,aCoordinate.y-vChangeColor.r));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.r,aCoordinate.y+vChangeColor.r));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.r,aCoordinate.y-vChangeColor.r));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.r,aCoordinate.y+vChangeColor.r));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.g,aCoordinate.y-vChangeColor.g));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.g,aCoordinate.y+vChangeColor.g));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.g,aCoordinate.y-vChangeColor.g));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.g,aCoordinate.y+vChangeColor.g));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.b,aCoordinate.y-vChangeColor.b));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.b,aCoordinate.y+vChangeColor.b));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.b,aCoordinate.y-vChangeColor.b));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.b,aCoordinate.y+vChangeColor.b));\n" +
                    "            nColor/=13.0;\n" +
                    "            gl_FragColor=nColor;\n" +
                    "        }else if(vChangeType==4){\n" +
                    "            float dis=distance(vec2(gPosition.x,gPosition.y/uXY),vec2(vChangeColor.r,vChangeColor.g));\n" +
                    "            if(dis<vChangeColor.b){\n" +
                    "                nColor=texture2D(vTexture,vec2(aCoordinate.x/2.0+0.25,aCoordinate.y/2.0+0.25));\n" +
                    "            }\n" +
                    "            gl_FragColor=nColor;\n" +
                    "        }else{\n" +
                    "            gl_FragColor=nColor;\n" +
                    "        }\n" +
                    "    }else{\n" +
                    "        gl_FragColor=nColor;\n" +
                    "    }\n" +
                    "}";*/

    private String vertexShaderCodes;
    private String fragmentShaderCodes;

    // ################# 为何使用坐标反了就不行了？？？？？ ############
    private final float[] vPos = {
//            -1.0f, 1f,
//            -1.0f, -1f,
//            1.0f, -1f,
//            1.0f, 1f,

            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f
    };
    // 0-1之间纹理间距
    private final float[] coords = {
//            0.0f, 0.0f,
//            1.0f, 0.0f,
//            1.0f, 1.0f,
//            0.0f, 1.0f

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    private FloatBuffer posBuffer, coordsBuffer;

    private float[] viewMatrix = new float[16];
    private float[] projectMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private int program;
    private int glHMatrix;
    private int glHPosition;
    private int glHCoordinate;
    private int glHTexture;
    private int hIsHalf;
    private int glHUxy;

    private Bitmap bitmap;
    private int textureId;
    private boolean isHalf;
    private float uXY;

    public AFilter(Context context, String vertex, String fragment) {
        this.context = context;
        this.vertexShaderCodes = vertex;
        this.fragmentShaderCodes = fragment;
        posBuffer = BufferUtils.arr2FloatBuffer(vPos);
        coordsBuffer = BufferUtils.arr2FloatBuffer(coords);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated: ");
        GLES20.glClearColor(1, 1, 1, 1);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        program = ShaderUtils.createProgram(context.getResources(), vertexShaderCodes, fragmentShaderCodes);

        glHPosition = GLES20.glGetAttribLocation(program, "vPosition");
        glHCoordinate = GLES20.glGetAttribLocation(program, "vCoordinate");
        glHMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        glHTexture = GLES20.glGetUniformLocation(program, "vTexture");

        hIsHalf = GLES20.glGetUniformLocation(program, "vIsHalf");
        glHUxy = GLES20.glGetUniformLocation(program, "uXY");
        onDrawCreatedSet(program);
    }

    protected abstract void onDrawSet();

    protected abstract void onDrawCreatedSet(int program);

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged: ");
        GLES20.glViewport(0, 0, width, height);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // 目的是达到图片居中的效果 ，centerInside
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        uXY = sWidthHeight;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(projectMatrix, 0,
                        -sWH * sWidthHeight, sWH * sWidthHeight, -1, 1, 3, 7);
            } else {
                Matrix.orthoM(projectMatrix, 0,
                        -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 7);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(projectMatrix, 0,
                        -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
            } else {
                Matrix.orthoM(projectMatrix, 0,
                        -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mvpMatrix, 0, projectMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.i(TAG, "onDrawFrame: ");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(program);
        onDrawSet();

        GLES20.glUniform1i(hIsHalf, isHalf ? 1 : 0);
        GLES20.glUniform1f(glHUxy, uXY);

        GLES20.glUniformMatrix4fv(glHMatrix, 1, false, mvpMatrix, 0);
        GLES20.glEnableVertexAttribArray(glHPosition);
        GLES20.glEnableVertexAttribArray(glHCoordinate);


        // 原先的代码
        // 创建texture
        textureId = createTexture();
        // 激活texture0通道，并设置绑定texture
        int textureType = 0;
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1f(glHTexture, textureType);

        /*
        //传入顶点坐标
        GLES20.glVertexAttribPointer(glHPosition, 2, GLES20.GL_FLOAT, false, 0, posBuffer);
        //传入纹理坐标
        GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 0, coordsBuffer);
        // 因为是2维的图形，所以需要顶点个数/2 即可
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vPos.length / 2);
        */

        // --------------------- 加入FBO start ---------------------
      /*

      // 1>  未绑定bitmap数据的纹理，绑定到FBO
        // 创建texture
        textureId = EasyGlUtils.genTexturesWithParameter(1, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight())[0];

        // 激活texture0通道，并设置绑定texture
        int textureType = 0;
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1f(glHTexture, textureType);

        // 2> 创建FBO ，并绑定纹理
        int fboId = createFBO();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); // 解绑纹理

        // 3>  绑定bitmap数据的纹理，会知道离屏里面去
        // 绑定FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        // 绑定数据到FBO的纹理里面去
        int imgTextureId = createTexture();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imgTextureId);


        // 解绑fbo
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        */

        // 上面执行的是绘制的数据存在FBO缓冲区里面去了，但是怎么显示呢？需要再次实现一个FBORenderer ,
        // 把textureId即FBO的textureId传入进去，就能实现缓冲区的显示


        // --------------------- 加入FBO end ---------------------

        //------------------------ 加入VBO start  ------------------
        // 创建VBO
        int bosId = createVertexBuffer();
        // 绑定显卡顶点
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bosId);


        //传入顶点坐标 ，或者传入顶点从显卡获取数据的起始位置
        GLES20.glVertexAttribPointer(glHPosition, 2, GLES20.GL_FLOAT, false, 4 * 2, 0); // 偏移量 一个间隔是4字节，-1 到1 偏移4*2=8
        //传入纹理坐标，或者传入顶点从显卡获取数据的起始位置
        GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 4 * 2, vPos.length * 4);
        // 因为是2维的图形，所以需要顶点个数/2 即可
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vPos.length / 2);

        //释放资源
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        // 解绑显卡顶点
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDisableVertexAttribArray(glHPosition);
        GLES20.glDisableVertexAttribArray(glHCoordinate);
        //------------------------ 加入VBO end  ------------------
    }

    // 绑定顶点，纹理顶点数据到vbo
    private int createVertexBuffer() {
        int[] vbos = new int[1];
        // 创建vbo
        GLES20.glGenBuffers(1, vbos, 0);
        // 绑定vbo
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbos[0]);
        // 分配vbo大小 , 最后一个参数是静态大小
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vPos.length * 4 + coords.length * 4, null, GLES20.GL_STATIC_DRAW);
        // 绑定顶点数据到显卡内存
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vPos.length * 4, posBuffer);
        // 绑定纹理顶点数据到显卡内存
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vPos.length * 4, coords.length * 4, coordsBuffer);
        // 解绑
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        return vbos[0];
    }

    // 创建纹理，并把纹理绑定到FBO
    private int createFBO() {
        int[] fbos = new int[1];
        // 创建
        GLES20.glGenFramebuffers(1, fbos, 0);
        // 绑定
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbos[0]);
        // 设置FBO大小，空数据
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, null);
        //绑定纹理到FBO
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureId, 0);
        //检查绑定是否成功
        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            // 绑定失败
        }
        // 解绑
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return fbos[0];
    }


    private int createTexture() {
        if (bitmap != null && !bitmap.isRecycled()) {
            int[] texture = new int[1];
            // 生成纹理
            GLES20.glGenTextures(1, texture, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);// 解绑
            return texture[0];
        }
        return 0;
    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setHalf(boolean half) {
        isHalf = half;
    }
}
