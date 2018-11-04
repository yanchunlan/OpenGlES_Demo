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
 * desc: 圆柱
 */
public class Cylinder extends BaseRenderer {
    private FloatBuffer vertexBuffer;
    float conePositions[];
    private float height = 2f;
    private int mProgram;

    private float[] projectMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private int matrixHandler;
    private int positionHandler;


    // 借用以前的代码
    private Oval ovalTop, ovalBottom;

    public Cylinder(@NotNull View view) {
        super(view);

        ovalBottom = new Oval(view);
        ovalTop = new Oval(view, height);

        conePositions = createVertexCoords();
        vertexBuffer = BufferUtils.arr2FloatBuffer(conePositions);

        mProgram = ShaderUtils.createProgram(view.getResources(),
                "vshader/Cone.sh",
                "fshader/Cone.sh");
    }

    private float[] createVertexCoords() {
        int r = 1;

        ArrayList<Float> data = new ArrayList<Float>();
        float step = (float) (Math.PI * 2 / 18);
        for (float i = 0; i < Math.PI * 2 + step; i += step) {
            data.add((float) (r * Math.cos(i)));
            data.add((float) (r * Math.sin(i)));
            data.add(height);
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

        ovalBottom.onSurfaceCreated(gl, config);
        ovalTop.onSurfaceCreated(gl, config);
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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, conePositions.length / 3);
        GLES20.glDisableVertexAttribArray(positionHandler);

        ovalBottom.setMatrix(mvpMatrix);
        ovalBottom.onDrawFrame(gl);
        ovalTop.setMatrix(mvpMatrix);
        ovalTop.onDrawFrame(gl);
    }
}
