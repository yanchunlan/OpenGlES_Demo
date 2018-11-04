package com.opengles.book.es2_0.render.render;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.View;

import com.opengles.book.es2_0.base.BaseRenderer;
import com.opengles.book.es2_0.utils.BufferUtils;
import com.opengles.book.es2_0.utils.ShaderUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-04 21:35
 * desc: 球体
 */
public class Ball extends BaseRenderer {
    private FloatBuffer vertexBuffer;
    float conePositions[];
    private int mProgram;

    private float[] projectMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private int matrixHandler;
    private int positionHandler;

    public Ball(@NotNull View view) {
        super(view);

        conePositions = createVertexCoords();
        vertexBuffer = BufferUtils.arr2FloatBuffer(conePositions);

        mProgram = ShaderUtils.createProgram(view.getResources(),
                "vshader/Ball.sh",
                "fshader/Cone.sh");
    }

    private float[] createVertexCoords() {
        ArrayList<Float> data = new ArrayList<Float>();
        float step = 5f;
        // 经度 -90-90 再维度0-360
        float r1, r2;
        float h1, h2;
        float sin, cos;
        for (float i = -90.0f; i < 90.0f + step; i += step) {
            r1 = (float) Math.cos(i * Math.PI / 180.0); // step是数字，装换维角度需要 x PI/180
            r2 = (float) Math.cos((i + step) * Math.PI / 180.0);
            h1 = (float) Math.sin(i * Math.PI / 180.0);
            h2 = (float) Math.sin((i + step) * Math.PI / 180.0);
            float step2 = 2 * step;
            for (float j = 0.0f; j < 360.0f + step2; j += step2) {
                cos = (float) Math.cos(j * Math.PI / 180.0);
                sin = (float) Math.sin(j * Math.PI / 180.0);

                /*data.add(r2 * cos);
                data.add(r2 * sin);
                data.add(h2);
                data.add(r1 * cos);
                data.add(r1 * sin);
                data.add(h1);*/

                data.add(r2 * cos);
                data.add(h2);
                data.add(r2 * sin);
                data.add(r1 * cos);
                data.add(h1);
                data.add(r1 * sin);
            }
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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, conePositions.length / 3);
        GLES20.glDisableVertexAttribArray(positionHandler);
    }
}
