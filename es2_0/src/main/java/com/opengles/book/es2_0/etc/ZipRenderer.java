package com.opengles.book.es2_0.etc;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.ETC1;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.opengles.book.es2_0.utils.BufferUtils;
import com.opengles.book.es2_0.utils.MatrixUtils;

import java.nio.ByteBuffer;
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
    private static final String TAG = "ZipRenderer";
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
    private float[] vertexCoords = {
            -1f, 1f,
            -1f, -1f,
            1f, 1f,
            1f, -1f
    };
    private short[] coords = {
            0, 0,
            0, -1,
            1, 0,
            1, 1
    };
    /*private float[] colors = {};   不需要color ,因为颜色是从 simple2D的texture2D生成*/
    private FloatBuffer vertexBuffer;
    private ShortBuffer coordBuffer;
    private FloatBuffer colorBuffer;

    public static final int TYPE = 0x01;
    private StateChangeListener mChangeListener;
    private Bitmap mBitmap;

    private int program;
    private int glPosition;
    private int glMatrix;
    private int glCoords;
    private int glTexture;
    private int glTextureAlpha;

    // 4维坐标，4x4=16
   /* private float[] projectMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];*/

    // params
    private ZipPkmReader mPkmReader;
    private int[] texture;

    private boolean isPlay = false;
    private GLSurfaceView mView;
    private int timeStep = 50;
    private float[] SM = MatrixUtils.getOriginalMatrix(); // 单位矩阵
    private int type = MatrixUtils.TYPE_CENTERINSIDE;
    private ByteBuffer emptyBuffer;
    private int width, height;


    public ZipRenderer(Context context) {
        mPkmReader = new ZipPkmReader(context);

        vertexBuffer = BufferUtils.arr2FloatBuffer(vertexCoords);
        coordBuffer = BufferUtils.arr2ShortBuffer(coords);
        program = createProgram(vertexShaderCodes, fragmentShaderCodes);

        glPosition = GLES20.glGetAttribLocation(program, "vPosition");
        glCoords = GLES20.glGetAttribLocation(program, "vCoords");
        glMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        glTexture = GLES20.glGetUniformLocation(program, "vTexture");
        glTextureAlpha = GLES20.glGetUniformLocation(program, "vTextureAlpha");

        texture = new int[2];
        createTextureId(texture);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.emptyBuffer = ByteBuffer.allocateDirect(ETC1.getEncodedDataSize(width, height));
        this.width = width;
        this.height = height;
        // 开启混合
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
       /* GLES20.glViewport(0, 0, width, height);
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(projectMatrix, 0, -sWH * sWidthHeight, sWH * sWidthHeight,
                        -1, 1, 3, 7);
            } else {
                Matrix.orthoM(projectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH,
                        -1, 1, 3, 7);
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
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 5, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectMatrix, 0, viewMatrix, 0);*/
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(program);

        bindTextureId();

        GLES20.glEnableVertexAttribArray(glPosition);
        GLES20.glVertexAttribPointer(glPosition, vertexCoords.length / 2,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(glCoords);
        GLES20.glVertexAttribPointer(glPosition, coords.length / 2,
                GLES20.GL_FLOAT, false,
                0, coordBuffer);

        // 绘制正方形，不是绘制纹理的坐标
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCoords.length / 2);
        GLES20.glDisableVertexAttribArray(glPosition);
        GLES20.glDisableVertexAttribArray(glCoords);
    }

    private void bindTextureId() {
        ETC1Util.ETC1Texture t = mPkmReader.getNextTexture();
        ETC1Util.ETC1Texture tAlpha = mPkmReader.getNextTexture();
        if (t != null && tAlpha != null) {
            MatrixUtils.getMatrix(SM, type, t.getWidth(), t.getHeight(), width, height);
            GLES20.glUniformMatrix4fv(glMatrix, 1, false, SM, 0);

            // bind  texture
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + getTextureType()); // 设置texture0通道
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            // etc需要的load加载
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_SHORT_5_6_5, t);
            GLES20.glUniform1i(glTexture, getTextureType());

            // bind  textureAlpha
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + getTextureType()); // 设置texture0通道
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[1]);
            // etc需要的load加载
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_SHORT_5_6_5, tAlpha);
            GLES20.glUniform1i(glTextureAlpha, 1 + getTextureType());
        } else {
            // 如果是null，则赋值空对象的 ETC1Util.ETC1Texture
            GLES20.glUniformMatrix4fv(glMatrix, 1, false, SM, 0);

            // bind  texture
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + getTextureType()); // 设置texture0通道
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            // etc需要的load加载
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_SHORT_5_6_5, new ETC1Util.ETC1Texture(width,height,emptyBuffer));
            GLES20.glUniform1i(glTexture, getTextureType());

            // bind  textureAlpha
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + getTextureType()); // 设置texture0通道
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[1]);
            // etc需要的load加载
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_SHORT_5_6_5, new ETC1Util.ETC1Texture(width,height,emptyBuffer));
            GLES20.glUniform1i(glTextureAlpha, 1 + getTextureType());
        }
    }

    public final int getTextureType() {
        return 0;
    }

    private void createTextureId(int[] texture) {
        GLES20.glGenTextures(GLES20.GL_TEXTURE_2D, texture, 0);
        for (int i = 0; i < texture.length; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[i]);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

//            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        }
    }

    //  ----------------------  可封装方法 start -------------------
    // 重新写一遍，熟悉代码
    private int createProgram(String vertexShaderCodes, String fragmentShaderCodes) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCodes);
        if (vertexShader == 0) return 0;
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCodes);
        if (vertexShader == 0) return 0;

        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        // 成功之后获取状态判断是否ok
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_COMPILE_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "loadShader: " + GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    private int loadShader(int type, String msg) {
        int shader = GLES20.glCreateShader(type);
        if (0 != shader) {
            GLES20.glShaderSource(shader, msg);
            GLES20.glReleaseShaderCompiler();
            // 成功之后获取状态判断是否ok
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "loadShader: " + type);
                Log.e(TAG, "loadShader: " + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    //  -------------------------  other start --------------------------

    public void setScaleType(int type, int... params) {
        if (type == TYPE) {
            this.type = params[0];
        }
    }

    public void setAnimation(ZipGlSurfaceView zipGlSurfaceView, String path, int timeStep) {
        this.mView = zipGlSurfaceView;
        this.timeStep = timeStep;
        mPkmReader.setPath(path);
    }

    public void start() {
        if (!isPlay) {
            stop();
            isPlay = true;
            // 状态改变为start
            changeState(StateChangeListener.STOP, StateChangeListener.START);
            if (mPkmReader != null) {
                mPkmReader.open();
            }
            // 请求重绘
            mView.requestRender();
        }
    }

    public void stop() {
        if (mPkmReader != null) {
            mPkmReader.close();
        }
        isPlay = false;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setChangeListener(StateChangeListener changeListener) {
        mChangeListener = changeListener;
    }

    private void changeState(int lastState, int nowState) {
        if (mChangeListener != null) {
            mChangeListener.onStateChanged(lastState, nowState);
        }
    }
}
