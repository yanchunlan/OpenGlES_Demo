package com.opengles.book.es2_0.render.render;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.View;

import com.opengles.book.es2_0.render.base.BaseRenderer;
import com.opengles.book.es2_0.utils.BufferUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-10-31 0:35
 * desc: 园
 */
public class Oval extends BaseRenderer {

    private String vertexShaderCodes =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "void main(){" +
                    "gl_Position=vMatrix*vPosition;" +
                    "}";
    private String fragmentShaderCodes =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    "gl_FragColor=vColor;" +
                    "}";

    private float vertexCoords[];
    private float height = 0;

    private float color[] = {1, 0, 0, 1f};

    private FloatBuffer vertexBuffer;

    private int program;

    private int positionHandler;
    private int colorHandler;
    private int matrixHandler;

    private float[] projectMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    public Oval(@NotNull View view, float height) {
        this(view);
        this.height = height;
    }

    public Oval(@NotNull View view) {
        super(view);
        vertexCoords = createVertexCoords();
        vertexBuffer = BufferUtils.arr2FloatBuffer(vertexCoords);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCodes);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCodes);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
    }

    // 其他图形 ，主要是顶点的计算，其他都是一样的操作
    private float[] createVertexCoords() {
        int n = 360;
        float r = 1.0f;

        ArrayList<Float> data = new ArrayList<>();
        // 中心点
        data.add(0f);
        data.add(0f);
        data.add(height);
     /*   float angDegSpan = 360f / n;
        for (int i = 0; i < 360 + angDegSpan; i += angDegSpan) {
            data.add((float) (r * Math.cos(i * 2 * Math.PI / 360f)));
            data.add((float) (r * Math.sin(i * 2 * Math.PI / 360f)));
            data.add(0f);
        }*/

        float step = (float) (Math.PI * 2 / 36f);
        for (float i = 0; i < Math.PI * 2+step; i += step) {
            data.add((float) (r * Math.cos(i)));
            data.add((float) (r * Math.sin(i)));
            data.add(height);
        }

      /*  for (float i = 0; i <= Math.PI * 2; i += step) {
            data.add((float) (r * Math.cos(i)));
            data.add((float) (r * Math.sin(i)));
            data.add(0f);
        }*/

        float[] f = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            f[i] = data.get(i);
        }
        return f;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = (float) width / height;
        Matrix.frustumM(projectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 7, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectMatrix, 0, viewMatrix, 0);

    }


    /*
     *
    int GL_POINTS       //将传入的顶点坐标作为单独的点绘制
    int GL_LINES        //将传入的坐标作为单独线条绘制，ABCDEFG六个顶点，绘制AB、CD、EF三条线
    int GL_LINE_STRIP   //将传入的顶点作为折线绘制，ABCD四个顶点，绘制AB、BC、CD三条线
    int GL_LINE_LOOP    //将传入的顶点作为闭合折线绘制，ABCD四个顶点，绘制AB、BC、CD、DA四条线。
    int GL_TRIANGLES    //将传入的顶点作为单独的三角形绘制，ABCDEF绘制ABC,DEF两个三角形
    int GL_TRIANGLE_FAN    //将传入的顶点作为扇面绘制，ABCDEF绘制ABC、ACD、ADE、AEF四个三角形
    int GL_TRIANGLE_STRIP   //将传入的顶点作为三角条带绘制，ABCDEF绘制ABC,BCD,CDE,DEF四个三角形
     *
     */
    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glUseProgram(program);

        matrixHandler = GLES20.glGetUniformLocation(program, "vMatrix");
        GLES20.glUniformMatrix4fv(matrixHandler, 1, false, mvpMatrix, 0);

        positionHandler = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandler);
        GLES20.glVertexAttribPointer(positionHandler, 3,
                GLES20.GL_FLOAT, false,
                0, // 不设置偏移量
                vertexBuffer);

        colorHandler = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(colorHandler, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCoords.length / 3);
        GLES20.glDisableVertexAttribArray(positionHandler);
    }

    public void setMatrix(float[] matrix){
        this.mvpMatrix=matrix;
    }
}
