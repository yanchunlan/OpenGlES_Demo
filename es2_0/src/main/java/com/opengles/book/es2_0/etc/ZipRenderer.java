package com.opengles.book.es2_0.etc;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.opengles.book.es2_0.utils.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-12 22:22
 * desc:
 */
public class ZipRenderer implements GLSurfaceView.Renderer {

    private String vertexShaderCodes = "attribute vec4 vPosition;" +
            "attribute vec2 vCoord;" +
            "varying vec2 aCoord;" +
            "uniform mat4 vMatrix;" +
            "void main(){" +
            "gl_Position=vMatrix*vPosition;" +
            "aCoord=vCoord;" +
            "}";
    private String fragmentShaderCodes = "precision medium float;" +
            "varying vec2 aCoord;" +
            "uniform sample2D vTexture;" +
            "uniform sample2D vTextureAlpha;" +
            "void main(){" +
            "vec4 color=texture2D(vTexture,aCoord);" +
            "color.a=texture2D(vTextureAlpha,aCoord).r;" + // 透明通道的r 赋值给color.a
            "gl_Color=color;" +
            "}";

    private int program;

    private Bitmap mBitmap;

    private float[] vertexCoords = {};
    private short[] coords = {};
    private float[] colors = {};
    private FloatBuffer vertexBuffer;
    private ShortBuffer coordBuffer;
    private FloatBuffer colorBuffer;

    public static final int TYPE = 0x01;

    private StateChangeListener mChangeListener;


    public ZipRenderer() {
        vertexBuffer = BufferUtils.arr2FloatBuffer(vertexCoords);
        coordBuffer = BufferUtils.arr2ShortBuffer(coords);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCodes);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCodes);
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);


    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    }

    //  ----------------------  可封装方法 start -------------------
    // 重新写一遍，首先代码

    private int loadShader(int type, String msg) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, msg);
        GLES20.glReleaseShaderCompiler();
        return shader;
    }

    //  -------------------------  other start --------------------------

    public void setScaleType(int type, int... params) {

    }

    public void setAnimation(ZipGlSurfaceView zipGlSurfaceView, String path, int timeStep) {
    }

    public void start() {
    }

    public void stop() {
    }

    public boolean isPlay() {
        return false;
    }

    public void setChangeListener(StateChangeListener changeListener) {
        mChangeListener = changeListener;
    }
}
