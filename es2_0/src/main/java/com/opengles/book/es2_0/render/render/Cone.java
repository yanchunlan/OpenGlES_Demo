package com.opengles.book.es2_0.render.render;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.View;

import com.opengles.book.es2_0.render.base.BaseRenderer;
import com.opengles.book.es2_0.utils.BufferUtils;
import com.opengles.book.es2_0.utils.ShaderUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-10-31 0:35
 * desc: 圆锥
 */
public class Cone extends BaseRenderer {
    /*private String vertexShaderCodes =
            "attribute vec4 vPosition;" +
            "uniform mat4 vMatrix;" +
            "varying vec4 vColor;" +
            "attribute vec4 aColor;" +
            "void main(){" +
            "gl_Position=vMatrix*vPosition;" +
            "if(vPosition.z!=0.0){" +
            "vColor=vec4(0.0,0.0,0.0,1.0);" +
            "}else{" +
            "vColor=aColor;" +
            "}" +
            "}";
    private String fragmentShaderCodes =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main(){" +
                    "gl_FragColor=vColor;" +
                    "}";*/

    private FloatBuffer vertexBuffer, colorBuffer;
    float conePositions[];
    float color[] = {1, 1, 1, 1};
    private int mProgram;

    private float[] projectMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private int matrixHandler;
    private int positionHandler;
    private int colorHandler;


    // 借用以前的代码
    private Oval oval;

    public Cone(@NotNull View view) {
        super(view);

        oval = new Oval(view);
        conePositions = createVertexCoords();
        vertexBuffer = BufferUtils.arr2FloatBuffer(conePositions);
        colorBuffer = BufferUtils.arr2FloatBuffer(color);

      /*  int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCodes);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCodes);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);*/
      // 此处使用.sh存储的文件设置顶点，片元着色器
        mProgram= ShaderUtils.createProgram(view.getResources(),
                "vshader/Cone.sh",
                "fshader/Cone.sh");
    }

    private float[] createVertexCoords() {
        float height = 2;
        int r = 1;

        ArrayList<Float> data = new ArrayList<Float>();
        data.add(0f);
        data.add(0f);
        data.add(height);

        float step = (float) (Math.PI * 2 / 18);
        for (float i = 0; i < Math.PI * 2 + step; i += step) {
            data.add((float) (r * Math.cos(i)));
            data.add((float) (r * Math.sin(i)));
            data.add(0f);
        }
        float[] f = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            f[i] = data.get(i);
        }
        return f;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        oval.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = (float) width / height;
        Matrix.frustumM(projectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        Matrix.setLookAtM(viewMatrix, 0, 1, -10, -4, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectMatrix, 0, viewMatrix, 0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glUseProgram(mProgram);

        matrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(matrixHandler, 1, false, mvpMatrix, 0);

        positionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandler);
        GLES20.glVertexAttribPointer(positionHandler, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

//        colorHandler = GLES20.glGetAttribLocation(mProgram, "aColor");
//        GLES20.glEnableVertexAttribArray(colorHandler);
//        GLES20.glVertexAttribPointer(colorHandler, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, conePositions.length / 3);
        GLES20.glDisableVertexAttribArray(positionHandler);

        oval.setMatrix(mvpMatrix);
        oval.onDrawFrame(gl);
    }
}
