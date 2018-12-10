package com.opengles.book.es2_0_test.glsv;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.opengles.book.es2_0_test.base.BaseGLSurfaceView;
import com.opengles.book.es2_0_test.shape.square.Cube;
import com.opengles.book.es2_0_test.shape.square.Square;
import com.opengles.book.es2_0_test.shape.square.VaryMatrixCube;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 绘制正方形的GLSurfaceView
 */
public class SquareGLSurfaceView extends BaseGLSurfaceView {

    public SquareGLSurfaceView(Context context) {
        super(context);

        // setRenderer(new SquareRenderer()); // 绘制正方形
        // setRenderer(new CubeRenderer());  // 绘制立方体

        setRenderer(new VaryMatrixCubeRenderer());
    }

    class SquareRenderer implements GLSurfaceView.Renderer {

        Square square;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            square = new Square();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            square.onSurfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            square.draw();
        }
    }

    class CubeRenderer implements GLSurfaceView.Renderer {

        Cube cube;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            cube = new Cube();
            cube.onSurfaceCreated();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            cube.onSurfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            cube.draw();
        }
    }

    class VaryMatrixCubeRenderer implements GLSurfaceView.Renderer {

        VaryMatrixCube cube;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            cube = new VaryMatrixCube();
            cube.onSurfaceCreated();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            cube.onSurfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            cube.draw();
        }
    }


}
