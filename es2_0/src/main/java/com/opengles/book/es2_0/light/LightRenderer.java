package com.opengles.book.es2_0.light;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.opengles.book.es2_0.utils.BufferUtils;
import com.opengles.book.es2_0.utils.MatrixUtils;
import com.opengles.book.es2_0.utils.ShaderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author:  ycl
 * date:  2018/12/7 18:03
 * desc:
 */
public class LightRenderer implements GLSurfaceView.Renderer {

    private Resources res;
    private int glProgramId;

    private int glAPosition;
    private int glACoord;
    private int glANormal;

    private int glUMatrix;
    private int glUBaseColor;
    private int glULightColor;
    private int glUAmbientStrength;
    private int glUDiffuseStrength;
    private int glUSpecularStrength;
    private int glULightPosition;

    private float[] mvpMatrix; // 物体的矩阵
    private float[] lambMatrix; // 光源的矩阵
    private FloatBuffer vertexBuffer;
    private float lx = 0f, ly = 0.8f, lz = -1f; // 光源位置


    private final float DEFAULT_AMBIENT = 0.3f;
    private final float DEFAULT_DIFFUSE = 0.5f;
    private final float DEFAULT_SPECULAR = 0.8f;

    private float ambientStrength = 0;
    private float diffuseStrength = 0;
    private float specularStrength = 0;


    // 包含了顶点数据，法线数据
    private final float[] data = new float[]{
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,

            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f
    };

    public LightRenderer(Resources res) {
        this.res = res;
        vertexBuffer = BufferUtils.arr2FloatBuffer(data);
    }

    public void setAmbientStrength(boolean isChecked) {
        this.ambientStrength = isChecked ? DEFAULT_AMBIENT : 0;
    }

    public void setDiffuseStrength(boolean isChecked) {
        this.diffuseStrength = isChecked ? DEFAULT_DIFFUSE : 0;
    }

    public void setSpecularStrength(boolean isChecked) {
        this.specularStrength = isChecked ? DEFAULT_SPECULAR : 0;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
        GLES20.glFrontFace(GLES20.GL_CW);

        glProgramId = ShaderUtils.createProgram(res, "light/light.vert", "light/light.frag");

        glAPosition = GLES20.glGetAttribLocation(glProgramId, "aPosition");
        glACoord = GLES20.glGetAttribLocation(glProgramId, "aCoord");
        glANormal = GLES20.glGetAttribLocation(glProgramId, "aNormal");

        glUMatrix = GLES20.glGetUniformLocation(glProgramId, "uMatrix");
        glUBaseColor = GLES20.glGetUniformLocation(glProgramId, "uBaseColor");
        glULightColor = GLES20.glGetUniformLocation(glProgramId, "uLightColor");

        glUAmbientStrength = GLES20.glGetUniformLocation(glProgramId, "uAmbientStrength");
        glUDiffuseStrength = GLES20.glGetUniformLocation(glProgramId, "uDiffuseStrength");
        glUSpecularStrength = GLES20.glGetUniformLocation(glProgramId, "uSpecularStrength");

        glULightPosition = GLES20.glGetUniformLocation(glProgramId, "uLightPosition");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 整体缩小0.5f，相当于置于正中心
        mvpMatrix = MatrixUtils.getOriginalMatrix();
        Matrix.scaleM(mvpMatrix, 0, 0.5f, 0.5f * width / (float) height, 0.5f);

        // 平移指定距离，确定光源位置，并整体缩小0.09
        lambMatrix = MatrixUtils.getOriginalMatrix();
        Matrix.translateM(lambMatrix, 0, lx, ly, lz);
        Matrix.scaleM(lambMatrix, 0, 0.09f, 0.09f * width / (float) height, 0.09f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        GLES20.glUseProgram(glProgramId);
        Matrix.rotateM(mvpMatrix, 0, 2, -1, -1, 1); // 绕xy旋转
        GLES20.glUniformMatrix4fv(glUMatrix, 1, false, mvpMatrix, 0);


        // ---------------- 设置光 start  -----------------
        //环境光强度
        GLES20.glUniform1f(glUAmbientStrength, ambientStrength);
        //漫反射光强度
        GLES20.glUniform1f(glUDiffuseStrength, diffuseStrength);
        //镜面光强度
        GLES20.glUniform1f(glUSpecularStrength, specularStrength);
        //光源颜色
        GLES20.glUniform3f(glULightColor, 0.0f, 0.0f, 1.0f);   // 物体默认颜色是blue
        //物体颜色
        GLES20.glUniform4f(glUBaseColor, 1.0f, 1.0f, 1.0f, 1.0f); // 物体默认颜色是黑色
        //光源位置
        GLES20.glUniform3f(glULightPosition, lx, ly, lz);
        // ---------------- 设置光 end  -----------------


        //传入顶点信息
        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(glAPosition);
        GLES20.glVertexAttribPointer(glAPosition, 3, GLES20.GL_FLOAT, false,
                6*4, //  跨度，一般情况下写0系统会自动识别。识别方式为size*sizeof(数组定义时报类型) 6*4，此处不传跨度会绘制不出来图形，不知为何？？？
                vertexBuffer);

        //传入法线信息
        vertexBuffer.position(3);
        GLES20.glEnableVertexAttribArray(glANormal);
        GLES20.glVertexAttribPointer(glANormal, 3, GLES20.GL_FLOAT, false,
                6*4, //  跨度，一般情况下写0系统会自动识别。识别方式为size*sizeof(数组定义时报类型) 6*4，此处不传跨度会绘制不出来图形，不知为何？？？
                vertexBuffer);

        // 绘制缓冲区的图形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,data.length/6);

        // 再绘制一个立方体，标记光源位置
        GLES20.glUniformMatrix4fv(glUMatrix,1,false,lambMatrix,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,data.length/6);

        //释放资源
//        GLES20.glDisable(GLES20.GL_CULL_FACE);
//        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisableVertexAttribArray(glAPosition);
        GLES20.glDisableVertexAttribArray(glANormal);

    }
}
